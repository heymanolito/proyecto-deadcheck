package com.example.application.ui.users;

import com.example.application.backend.data.entity.Status;
import com.example.application.backend.data.entity.Tracking;
import com.example.application.backend.data.entity.User;
import com.example.application.backend.data.service.UserService;
import com.example.application.ui.MainLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.gridpro.GridPro;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.annotation.security.RolesAllowed;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.application.backend.data.util.DateUtil.formatDate;

@PageTitle("Deacheck | User Tracking")
@Route(value = "tracking", layout = MainLayout.class)
@RolesAllowed("USER")
public class UserCheckIn extends Div {

    private GridPro<User> grid;
    private GridListDataView<User> gridListDataView;
    private final UserService userService;
    private User currentUser;
    private Binder<User> binder = new BeanValidationBinder<>(User.class);

    private Grid.Column<User> nameColumn;
    private Grid.Column<User> departmentColumn;
    private Grid.Column<User> statusColumn;
    private Grid.Column<User> emailColumn;

    public UserCheckIn(UserService userService) {
        this.userService = userService;
        addClassName("users-view");
        setSizeFull();
        createGrid();
        add(grid);
    }

    private void createGrid() {
        createGridComponent();
        addColumnsToGrid();

    }

    private void setUser(User user) {
        this.currentUser = user;
        binder.readBean(user);
    }

    private void validateAndSave() {
        try {
            binder.writeBean(currentUser);
            setUser(currentUser);
            userService.update(currentUser);
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }

    private void createGridComponent() {
        grid = new GridPro<>();
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_COLUMN_BORDERS);
        grid.setHeight("100%");

        Authentication userAuth = SecurityContextHolder.getContext().getAuthentication();
        currentUser = userService.findByUsername(userAuth.getName());

        Set<User> user = Collections.singleton(currentUser);
        gridListDataView = grid.setItems(user);
    }


    private void addColumnsToGrid() {
        createClientColumn();
        createEmailColumn();
        createDepartmentColumn();
        createStatusColumn();

    }

    private void createClientColumn() {
        nameColumn = grid.addColumn(new ComponentRenderer<>(user -> {
            HorizontalLayout hl = new HorizontalLayout();
            hl.setAlignItems(FlexComponent.Alignment.CENTER);
            Image img = new Image(user.getProfilePictureUrl(), "");
            Span span = new Span();
            span.setClassName("name");
            span.setText(user.getName() + " " + user.getSurname());
            hl.add(img, span);
            return hl;
        })).setComparator(user -> user.getName() + " " + user.getSurname()).setHeader("Nombre");
    }

    private void createStatusColumn() {
        statusColumn = grid.addColumn(new ComponentRenderer<>(user -> {
            Select<Status> select = new Select<>();
            select.setItems(Status.values());
            select.setPlaceholder("Seleccione un estado");
            select.setValue(user.getStatus());
            select.setLabel("Status");
            select.setWidth("100%");
            select.addValueChangeListener(event -> {
                user.setStatus(event.getValue());
                //comprobar si hay un parte abierto. Si no lo hay, crear uno nuevo.
                List<Tracking> trackings = user.getTrackingList();
                if(trackings.get(trackings.size() -1).getWorkCheckIn() != null &&
                        trackings.get(trackings.size() -1).getWorkCheckOut() != null) {
                    Tracking tracking = new Tracking();
                    switchStatus(user, tracking);
                    validateAndSave();
                } else if (trackings.get(trackings.size() -1).getWorkCheckIn() == null &&
                        trackings.get(trackings.size() -1).getWorkCheckOut() == null
                        || trackings.get(trackings.size() -1).getWorkCheckIn() != null
                        && trackings.get(trackings.size() -1).getWorkCheckOut() == null
                        && user.getTrackingList().size() > 0) {
                    Tracking tracking = user.getTrackingList().get(trackings.size() - 1);
                    switchStatus(user, tracking);
                    validateAndSave();
                }
            });
            return select;
        })).setComparator(user -> user.getStatus().toString()).setHeader("Status");

    }

    private void switchStatus(User user, Tracking tracking) {
        if(user.getStatus() == Status.Entrada){
            tracking.setWorkCheckIn(LocalDateTime.now());
            tracking.setUser(user);
            user.getTrackingList().add(tracking);
        } else if (user.getStatus() == Status.Salida){
            tracking.setWorkCheckOut(LocalDateTime.now());
            tracking.setUser(user);
            user.getTrackingList().add(tracking);
        }
    }

    private void createDepartmentColumn() {
        departmentColumn = grid.addColumn(User::getDepartment).setHeader("Departamento");
    }

    private void createEmailColumn() {
        emailColumn = grid.addColumn(User::getEmail).setHeader("Email");
    }

    private String setBadge(Status status) {
        return switch (status) {
            case Entrada-> "badge success";
            case Salida -> "badge error";
            case Vacaciones -> "badge pending";
        };
    }

}
