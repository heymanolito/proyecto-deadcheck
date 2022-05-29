package com.example.application.backend.security;

import com.example.application.ui.login.LoginView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.internal.AnnotationReader;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.internal.RouteUtil;
import com.vaadin.flow.server.auth.ViewAccessChecker;
import com.vaadin.flow.spring.security.VaadinSavedRequestAwareAuthenticationSuccessHandler;
import com.vaadin.flow.spring.security.VaadinWebSecurityConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;

import java.util.Optional;

@EnableWebSecurity
@Configuration
public class SecurityConfiguration extends VaadinWebSecurityConfigurerAdapter {

    private final AuthenticationSuccessHandler authenticationSuccessHandler;


    public static final String LOGOUT_URL = "/";

    @Autowired
    private ViewAccessChecker viewAccessChecker;

    public SecurityConfiguration(AuthenticationSuccessHandler authenticationSuccessHandler) {
        this.authenticationSuccessHandler = authenticationSuccessHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        setLoginView(http, LoginView.class, LOGOUT_URL);


    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
        web.ignoring().antMatchers("/images/*.png");

    }


    private VaadinSavedRequestAwareAuthenticationSuccessHandler getVaadinSavedRequestAwareAuthenticationSuccessHandler(HttpSecurity http) {
        VaadinSavedRequestAwareAuthenticationSuccessHandler vaadinSavedRequestAwareAuthenticationSuccessHandler = new VaadinSavedRequestAwareAuthenticationSuccessHandler();
        vaadinSavedRequestAwareAuthenticationSuccessHandler.setDefaultTargetUrl(this.applyUrlMapping("/management"));
        RequestCache requestCache = http.getSharedObject(RequestCache.class);
        if (requestCache != null) {
            vaadinSavedRequestAwareAuthenticationSuccessHandler.setRequestCache(requestCache);
        }

        return vaadinSavedRequestAwareAuthenticationSuccessHandler;
    }

    @Override
    protected void setLoginView(HttpSecurity http, Class<? extends Component> flowLoginView, String logoutUrl) throws Exception {
        Optional<Route> route = AnnotationReader.getAnnotationFor(flowLoginView, Route.class);
        if (route.isEmpty()) {
            throw new IllegalArgumentException("Unable find a @Route annotation on the login view " + flowLoginView.getName());
        } else {
            String loginPath = RouteUtil.getRoutePath(flowLoginView, (Route)route.get());
            if (!loginPath.startsWith("/")) {
                loginPath = "/" + loginPath;
            }

            loginPath = this.applyUrlMapping(loginPath);
            FormLoginConfigurer<HttpSecurity> formLogin = http.formLogin();
            formLogin.loginPage(loginPath).permitAll();
            formLogin.successHandler(authenticationSuccessHandler);
            http.csrf().ignoringAntMatchers(new String[]{loginPath});
            http.logout().logoutSuccessUrl(logoutUrl);
            http.exceptionHandling().defaultAuthenticationEntryPointFor(new LoginUrlAuthenticationEntryPoint(loginPath), AnyRequestMatcher.INSTANCE);
            this.viewAccessChecker.setLoginView(flowLoginView);
        }
    }
}
