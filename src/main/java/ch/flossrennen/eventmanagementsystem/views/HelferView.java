package ch.flossrennen.eventmanagementsystem.views;

import ch.flossrennen.eventmanagementsystem.model.Helfer;
import ch.flossrennen.eventmanagementsystem.service.HelferService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;

@Route("helfer")
@Slf4j
public class HelferView extends VerticalLayout {

    private final HelferService helferService;
    
    private final Grid<Helfer> grid = new Grid<>(Helfer.class, false);
    private final Binder<Helfer> binder = new Binder<>(Helfer.class);
    
    private final TextField vornameField = new TextField("Vorname");
    private final TextField nachnameField = new TextField("Nachname");
    private final EmailField emailField = new EmailField("Email");
    private final TextField telefonField = new TextField("Telefon");
    
    private final Button saveButton = new Button("Speichern");
    private final Button cancelButton = new Button("Abbrechen");
    private final Button newButton = new Button("Neuer Helfer");
    
    private Helfer currentHelfer;

    public HelferView(HelferService helferService) {
        this.helferService = helferService;
        
        setSizeFull();
        
        configureGrid();
        configureForm();
        
        add(
            newButton,
            createFormLayout(),
            grid
        );
        
        updateList();
        closeEditor();
    }
    
    private void configureGrid() {
        grid.addColumn(Helfer::getId).setHeader("ID").setAutoWidth(true);
        grid.addColumn(Helfer::getVorname).setHeader("Vorname").setAutoWidth(true);
        grid.addColumn(Helfer::getNachname).setHeader("Nachname").setAutoWidth(true);
        grid.addColumn(Helfer::getEmail).setHeader("Email").setAutoWidth(true);
        grid.addColumn(Helfer::getTelefon).setHeader("Telefon").setAutoWidth(true);
        
        grid.asSingleSelect().addValueChangeListener(event -> editHelfer(event.getValue()));
    }
    
    private void configureForm() {
        vornameField.setRequired(true);
        vornameField.setMaxLength(100);
        
        nachnameField.setRequired(true);
        nachnameField.setMaxLength(100);
        
        emailField.setMaxLength(150);
        emailField.setClearButtonVisible(true);
        
        telefonField.setMaxLength(20);
        
        binder.forField(vornameField)
            .asRequired("Vorname ist erforderlich")
            .bind(Helfer::getVorname, Helfer::setVorname);
        
        binder.forField(nachnameField)
            .asRequired("Nachname ist erforderlich")
            .bind(Helfer::getNachname, Helfer::setNachname);
        
        binder.forField(emailField)
            .bind(Helfer::getEmail, Helfer::setEmail);
        
        binder.forField(telefonField)
            .bind(Helfer::getTelefon, Helfer::setTelefon);
        
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(event -> saveHelfer());
        
        cancelButton.addClickListener(event -> closeEditor());
        
        newButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        newButton.addClickListener(event -> addHelfer());
    }
    
    private VerticalLayout createFormLayout() {
        VerticalLayout formLayout = new VerticalLayout(
            vornameField,
            nachnameField,
            emailField,
            telefonField,
            new HorizontalLayout(saveButton, cancelButton)
        );
        formLayout.setVisible(false);
        return formLayout;
    }
    
    private void addHelfer() {
        grid.asSingleSelect().clear();
        editHelfer(new Helfer());
    }
    
    private void editHelfer(Helfer helfer) {
        if (helfer == null) {
            closeEditor();
        } else {
            currentHelfer = helfer;
            binder.readBean(helfer);
            getChildren().filter(c -> c instanceof VerticalLayout && c != grid)
                .findFirst().ifPresent(c -> c.setVisible(true));
        }
    }
    
    private void closeEditor() {
        currentHelfer = null;
        binder.readBean(null);
        getChildren().filter(c -> c instanceof VerticalLayout && c != grid)
            .findFirst().ifPresent(c -> c.setVisible(false));
        grid.asSingleSelect().clear();
    }
    
    private void saveHelfer() {
        try {
            if (currentHelfer == null) {
                currentHelfer = new Helfer();
            }
            binder.writeBean(currentHelfer);
            helferService.save(currentHelfer);
            updateList();
            closeEditor();
            
            Notification notification = Notification.show("Helfer erfolgreich gespeichert");
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (ValidationException e) {
            Notification notification = Notification.show("Bitte füllen Sie alle erforderlichen Felder aus");
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
    
    private void updateList() {
        grid.setItems(helferService.findAll());
    }
}
