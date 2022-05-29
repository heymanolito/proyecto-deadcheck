package com.example.application.ui.users;

import com.example.application.backend.data.entity.Department;
import com.example.application.backend.data.entity.Status;
import com.example.application.backend.data.entity.Tracking;
import com.example.application.backend.data.entity.User;
import com.example.application.backend.data.service.ReportService;
import com.example.application.backend.data.service.UserService;
import com.example.application.backend.data.util.AbstractReport;
import com.example.application.ui.MainLayout;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.crud.Crud;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.gridpro.GridPro;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.shared.Registration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;
import java.util.Locale;
import javax.annotation.security.RolesAllowed;

import static com.example.application.backend.data.util.AbstractReport.DEFAULT_FONT_SIZE;
import static com.example.application.backend.data.util.DateUtil.formatDate;
import static com.itextpdf.text.FontFactory.HELVETICA;
import static com.itextpdf.text.PageSize.A4;

@PageTitle("Deadcheck | User Management")
@Route(value = "management", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class UsersView extends Div {

    private Grid<User> grid;
    private GridListDataView<User> gridListDataView;
    private static final Logger log = LoggerFactory.getLogger(User.class);

    private final UserService userService;
    private final Anchor downloadWidget;
    private Grid.Column<User> nameColumn;
    private Grid.Column<User> emailColumn;
    private Grid.Column<User> departmentColumn;
    private Grid.Column<User> statusColumn;
    private Grid.Column<User> pdfColumn;
    private Component button;
    //Campos de texto
    private TextField profilePicture = new TextField("Foto de perfil");
    private TextField name = new TextField("Nombre");
    private TextField surname = new TextField("Apellidos");
    private TextField email = new TextField("Email");
    private ComboBox<Department> department= new ComboBox<>("Departamento", Department.values());
    private ComboBox<Status> status = new ComboBox<>("Estado", Status.values());
    private Binder<User> binder = new BeanValidationBinder<>(User.class);
    //Editor
    Button save = new Button("Guardar", VaadinIcon.CHECK.create());
    Button cancel = new Button("Cancelar");
    Button delete = new Button("Borrar", VaadinIcon.TRASH.create());
    HorizontalLayout actions = new HorizontalLayout(save, cancel, delete);
    private User user;

    public UsersView(UserService userService) {
        this.userService = userService;
        binder.bindInstanceFields(this);
        user = new User();
        downloadWidget = new Anchor();
        downloadWidget.getStyle().set("display", "none");
        downloadWidget.setTarget("_blank");

        addClassName("users-view");
        setSizeFull();
        createGrid();


        this.setupEditor();
        this.add(grid);
        this.add(downloadWidget);
    }

    private void setUser(User user) {
        this.user = user;
        binder.readBean(user);
    }

    private void validateAndSave() {
        try {
            binder.writeBean(user);
            setUser(user);
            userService.update(user);
            fireEvent(new SaveEvent(this, user));
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }



    private void setupSaveButton(Dialog dialog) {
        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setWidth("100%");
        buttons.setPadding(true);
        buttons.setSpacing(true);
        Button buttonSave = new Button("Nuevo empleado", e -> {
            dialog.open();
        });
        buttons.add(buttonSave);
        this.add(buttons);
    }

    private void setupEditor() {
        Dialog dialog = new Dialog();

        dialog.add(new H4("Nuevo usuario"));
        VerticalLayout content = createDialogLayout();


        Button save = createSaveButton(dialog);
        Button cancel = new Button("Cancelar", e -> dialog.close());

        HorizontalLayout menuButtons = new HorizontalLayout();
        menuButtons.setWidth("100%");
        //menuButtons.setPadding(true);
        menuButtons.setPadding(true);
        menuButtons.setSpacing(true);
        menuButtons.add(save, cancel);
        dialog.add(content);
        dialog.add(menuButtons);


        this.setupSaveButton(dialog);
        this.add(dialog);
    }

    private Button createSaveButton(Dialog dialog) {
        Button saveButton = new Button("Save", e -> {
            validateAndSave();

            dialog.close();
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        return saveButton;
    }

    private VerticalLayout createDialogLayout() {

        VerticalLayout dialogLayout  = new VerticalLayout(profilePicture, name, surname, email, department, status);
        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "18rem").set("max-width", "100%");
        return dialogLayout;
    }


    private void createGrid() {
        createGridComponent();
        addColumnsToGrid();
    }


    private void createGridComponent() {
        grid = new GridPro<>();
        grid.setSelectionMode(SelectionMode.MULTI);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_COLUMN_BORDERS);
        grid.setHeight("100%");
        List<User> users = userService.findAll();
        gridListDataView = grid.setItems(users);

    }

    private void addColumnsToGrid() {
        createNameColumn();
        createEmailColumn();
        createDepartmentColumn();
        createStatusColumn();
        createPDFColumn();
    }

    private void createNameColumn() {
        nameColumn = grid.addColumn(new ComponentRenderer<>(user -> {
            HorizontalLayout hl = new HorizontalLayout();
            hl.setAlignItems(Alignment.CENTER);
            Image img = new Image(user.getProfilePictureUrl(), "");
            Span span = new Span();
            span.setClassName("name");
            span.setText(user.getName() + " " + user.getSurname());
            hl.add(img, span);
            return hl;
        })).setComparator(user -> user.getName() + " " + user.getSurname()).setHeader("Nombre");
    }

    private void createDepartmentColumn() {
        departmentColumn = grid.addColumn(User::getDepartment).setHeader("Departamento");
    }

    private void createEmailColumn() {
        emailColumn = grid.addColumn(User::getEmail).setHeader("Email");
    }

    private void createStatusColumn() {

        statusColumn = grid.addColumn(new ComponentRenderer<>(user -> {
            Span span = new Span();
            span.setText(user.getStatus().toString() + ": " + formatDate(userService.getLastAttendance(user)));
            span.getElement()
                    .setAttribute("theme", setBadge(user.getStatus()));
            return span;
        })).setComparator(User::getStatus).setHeader("Status");


    }

    private String setBadge(Status status) {
        return switch (status) {
            case Entrada-> "badge success";
            case Salida -> "badge error";
            case Vacaciones -> "badge pending";
        };
    }

    private void createPDFColumn() {
        pdfColumn = grid.addColumn(new ComponentRenderer<>(user -> {
            Button button = new Button("PDF", e -> {
                StreamResource streamResource = new StreamResource("tracking_report" + user.getUser_id() + ".pdf",
                        () -> new ByteArrayInputStream(userService.generatePDF(user)));
                download(streamResource);
            });
            button.setIcon(VaadinIcon.FILE.create());
            button.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
            return button;
        })).setHeader("PDF");

    }

    protected void download(StreamResource resource) {
        downloadWidget.setHref(resource);
        UI.getCurrent().getPage().executeJs("$0.click();", downloadWidget.getElement());
    }

    // Events
    public static abstract class UserFormEvent extends ComponentEvent<UsersView> {
        private final User user;

        protected UserFormEvent (UsersView source, User user) {
            super(source, false);
            this.user = user;
        }

        public User getUser() {
            return user;
        }
    }

    public static class SaveEvent extends UserFormEvent {
        SaveEvent(UsersView source, User user) {
            super(source, user);
        }
    }

    public static class DeleteEvent extends UserFormEvent {
        DeleteEvent(UsersView source, User user) {
            super(source, user);
        }
    }

    public static class CloseEvent extends UserFormEvent {
        CloseEvent(UsersView source, User user) {
            super(source, user);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}

//