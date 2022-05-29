package com.example.application.backend.data.generator;

import com.example.application.backend.data.entity.RoleEnum;
import com.example.application.backend.data.entity.Status;
import com.example.application.backend.data.entity.Tracking;
import com.example.application.backend.data.entity.User;
import com.example.application.backend.data.repository.TrackingRepository;
import com.example.application.backend.data.repository.UserRepository;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SpringComponent
public class DataGenerator {

    @Bean
    public CommandLineRunner loadData(PasswordEncoder passwordEncoder, UserRepository userRepository, TrackingRepository trackingRepository) {
        return args -> {
            Logger logger = LoggerFactory.getLogger(getClass());
            if (userRepository.count() != 0L) {
                logger.info("Using existing database");
                return;
            }
            int seed = 123;

            logger.info("... generating 2 User entities...");

            User gonzalo = User.builder()
                    .name("Gonzalo")
                    .surname("García Rodríguez")
                    .username("user")
                    .hashedPassword(passwordEncoder.encode("user"))
                    .email("gonzalo@deadcheck.es")
                    .roleEnums(Collections.singleton(RoleEnum.USER))
                    .profilePictureUrl("https://avatars3.githubusercontent.com/u/12097?s=460&v=4")
                    .status(Status.Entrada)
                    .build();

            List<Tracking> partesGonzalo = new ArrayList<>();
            Tracking trackingGonzalo = Tracking.builder()
                    .user(gonzalo)
                    .workCheckIn(LocalDateTime.now())
                    .build();
            //partesGonzalo.add(trackingGonzalo);

            userRepository.save(gonzalo);
            trackingRepository.save(trackingGonzalo);
            User manuel = User.builder()
                    .name("Manuel")
                    .surname("Gallardo Fuentes")
                    .username("admin")
                    .hashedPassword(passwordEncoder.encode("admin"))
                    .email("manuel@deadcheck.es")
                    .roleEnums(Collections.singleton(RoleEnum.ADMIN))
                    .profilePictureUrl("https://avatars3.githubusercontent.com/u/12097?s=460&v=4").status(Status.Salida)
                    .build();

            userRepository.save(manuel);

            List<Tracking> partesManuel = new ArrayList<>();
            Tracking trackingManuel = Tracking.builder()
                    .user(manuel)
                    .workCheckOut(LocalDateTime.now())
                    .build();

            trackingRepository.save(trackingManuel);
            //partesGonzalo.add(trackingManuel);


            logger.info("Generated demo data");
        };
    }

}