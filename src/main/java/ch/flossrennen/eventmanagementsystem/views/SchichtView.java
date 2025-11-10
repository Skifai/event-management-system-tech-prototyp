package ch.flossrennen.eventmanagementsystem.views;

import ch.flossrennen.eventmanagementsystem.model.Schicht;
import ch.flossrennen.eventmanagementsystem.service.SchichtService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Route("schichten")
@Slf4j
public class SchichtView extends VerticalLayout {

    private final SchichtService schichtService;
    
    private final Grid<Schicht> grid = new Grid<>(Schicht.class, false);
    private final Binder<Schicht> binder = new Binder<>(Schicht.class);
    
    private final TextField nameField = new TextField("Name");
    private final DateTimePicker startzeitField = new DateTimePicker("Startzeit");
    private final DateTimePicker endzeitField = new DateTimePicker("Endzeit");
    private final TextArea beschreibungField = new TextArea("Beschreibung");
    
    private final Button saveButton = new Button("Speichern");
    private final Button cancelButton = new Button("Abbrechen");
    private final Button newButton = new Button("Neue Schicht");
    
    private Schicht currentSchicht;

    public SchichtView(SchichtService schichtService) {
        this.schichtService = schichtService;
        
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm", Locale.GERMAN);
        
        grid.addColumn(Schicht::getId).setHeader("ID").setAutoWidth(true);
        grid.addColumn(Schicht::getName).setHeader("Name").setAutoWidth(true);
        grid.addColumn(s -> s.getStartzeit() != null ? s.getStartzeit().format(formatter) : "")
            .setHeader("Startzeit").setAutoWidth(true);
        grid.addColumn(s -> s.getEndzeit() != null ? s.getEndzeit().format(formatter) : "")
            .setHeader("Endzeit").setAutoWidth(true);
        grid.addColumn(Schicht::getBeschreibung).setHeader("Beschreibung").setAutoWidth(true);
        
        grid.asSingleSelect().addValueChangeListener(event -> editSchicht(event.getValue()));
    }
    
    private void configureForm() {
        nameField.setRequired(true);
        nameField.setMaxLength(100);
        
        startzeitField.setLocale(Locale.GERMAN);
        
        endzeitField.setLocale(Locale.GERMAN);
        
        beschreibungField.setMaxLength(300);
        beschreibungField.setHeight("80px");
        
        binder.forField(nameField)
            .asRequired("Name ist erforderlich")
            .bind(Schicht::getName, Schicht::setName);
        
        binder.forField(startzeitField)
            .asRequired("Startzeit ist erforderlich")
            .bind(Schicht::getStartzeit, Schicht::setStartzeit);
        
        binder.forField(endzeitField)
            .asRequired("Endzeit ist erforderlich")
            .bind(Schicht::getEndzeit, Schicht::setEndzeit);
        
        binder.forField(beschreibungField)
            .bind(Schicht::getBeschreibung, Schicht::setBeschreibung);
        
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(event -> saveSchicht());
        
        cancelButton.addClickListener(event -> closeEditor());
        
        newButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        newButton.addClickListener(event -> addSchicht());
    }
    
    private VerticalLayout createFormLayout() {
        VerticalLayout formLayout = new VerticalLayout(
            nameField,
            startzeitField,
            endzeitField,
            beschreibungField,
            new HorizontalLayout(saveButton, cancelButton)
        );
        formLayout.setVisible(false);
        return formLayout;
    }
    
    private void addSchicht() {
        grid.asSingleSelect().clear();
        editSchicht(new Schicht());
    }
    
    private void editSchicht(Schicht schicht) {
        if (schicht == null) {
            closeEditor();
        } else {
            currentSchicht = schicht;
            binder.readBean(schicht);
            getChildren().filter(c -> c instanceof VerticalLayout && c != grid)
                .findFirst().ifPresent(c -> c.setVisible(true));
        }
    }
    
    private void closeEditor() {
        currentSchicht = null;
        binder.readBean(null);
        getChildren().filter(c -> c instanceof VerticalLayout && c != grid)
            .findFirst().ifPresent(c -> c.setVisible(false));
        grid.asSingleSelect().clear();
    }
    
    private void saveSchicht() {
        try {
            if (currentSchicht == null) {
                currentSchicht = new Schicht();
            }
            binder.writeBean(currentSchicht);
            schichtService.save(currentSchicht);
            updateList();
            closeEditor();
            
            Notification notification = Notification.show("Schicht erfolgreich gespeichert");
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (ValidationException e) {
            Notification notification = Notification.show("Bitte füllen Sie alle erforderlichen Felder aus");
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (IllegalArgumentException e) {
            Notification notification = Notification.show(e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (Exception e) {
            Notification notification = Notification.show("Fehler beim Speichern: " + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
    
    private void updateList() {
        grid.setItems(schichtService.findAll());
    }
}
