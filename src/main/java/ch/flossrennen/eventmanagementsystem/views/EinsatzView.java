package ch.flossrennen.eventmanagementsystem.views;

import ch.flossrennen.eventmanagementsystem.model.Einsatz;
import ch.flossrennen.eventmanagementsystem.model.Helfer;
import ch.flossrennen.eventmanagementsystem.model.Ressort;
import ch.flossrennen.eventmanagementsystem.model.Schicht;
import ch.flossrennen.eventmanagementsystem.service.EinsatzService;
import ch.flossrennen.eventmanagementsystem.service.HelferService;
import ch.flossrennen.eventmanagementsystem.service.RessortService;
import ch.flossrennen.eventmanagementsystem.service.SchichtService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Route("einsaetze")
@Slf4j
public class EinsatzView extends VerticalLayout {

    private final EinsatzService einsatzService;
    private final RessortService ressortService;
    private final SchichtService schichtService;
    private final HelferService helferService;
    
    private final Grid<Einsatz> grid = new Grid<>(Einsatz.class, false);
    private final Binder<Einsatz> binder = new Binder<>(Einsatz.class);
    
    private final TextArea beschreibungField = new TextArea("Beschreibung");
    private final DateTimePicker startzeitField = new DateTimePicker("Startzeit");
    private final DateTimePicker endzeitField = new DateTimePicker("Endzeit");
    private final TextField ortField = new TextField("Ort");
    private final TextField mittelField = new TextField("Mittel");
    private final IntegerField benoetigteHelferField = new IntegerField("Benötigte Helfer");
    private final ComboBox<Ressort> ressortCombo = new ComboBox<>("Ressort");
    private final ComboBox<Schicht> schichtCombo = new ComboBox<>("Schicht (optional)");
    private final ComboBox<Einsatz.EinsatzStatus> statusCombo = new ComboBox<>("Status");
    
    private final Button saveButton = new Button("Speichern");
    private final Button cancelButton = new Button("Abbrechen");
    private final Button newButton = new Button("Neuer Einsatz");
    
    // Helper assignment components
    private final ComboBox<Helfer> helferCombo = new ComboBox<>("Helfer zuweisen");
    private final Button assignButton = new Button("Zuweisen");
    private final Grid<Helfer> assignedHelferGrid = new Grid<>(Helfer.class, false);
    
    private Einsatz currentEinsatz;
    private VerticalLayout formLayout;

    public EinsatzView(EinsatzService einsatzService, RessortService ressortService, 
                      SchichtService schichtService, HelferService helferService) {
        this.einsatzService = einsatzService;
        this.ressortService = ressortService;
        this.schichtService = schichtService;
        this.helferService = helferService;
        
        setSizeFull();
        
        configureGrid();
        configureForm();
        configureHelferAssignment();
        
        formLayout = createFormLayout();
        
        add(
            newButton,
            formLayout,
            grid
        );
        
        updateList();
        closeEditor();
    }
    
    private void configureGrid() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm", Locale.GERMAN);
        
        grid.addColumn(Einsatz::getId).setHeader("ID").setAutoWidth(true);
        grid.addColumn(Einsatz::getBeschreibung).setHeader("Beschreibung").setAutoWidth(true);
        grid.addColumn(e -> e.getStartzeit() != null ? e.getStartzeit().format(formatter) : "")
            .setHeader("Startzeit").setAutoWidth(true);
        grid.addColumn(e -> e.getEndzeit() != null ? e.getEndzeit().format(formatter) : "")
            .setHeader("Endzeit").setAutoWidth(true);
        grid.addColumn(e -> e.getRessort() != null ? e.getRessort().getName() : "")
            .setHeader("Ressort").setAutoWidth(true);
        grid.addColumn(e -> e.getZugewieseneHelfer().size() + "/" + e.getBenoetigteHelfer())
            .setHeader("Helfer").setAutoWidth(true);
        grid.addColumn(Einsatz::getStatus).setHeader("Status").setAutoWidth(true);
        
        grid.asSingleSelect().addValueChangeListener(event -> editEinsatz(event.getValue()));
    }
    
    private void configureForm() {
        beschreibungField.setRequired(true);
        beschreibungField.setMaxLength(300);
        beschreibungField.setHeight("80px");
        
        startzeitField.setLocale(Locale.GERMAN);
        
        endzeitField.setLocale(Locale.GERMAN);
        
        ortField.setMaxLength(150);
        mittelField.setMaxLength(200);
        
        benoetigteHelferField.setMin(0);
        benoetigteHelferField.setValue(1);
        benoetigteHelferField.setStepButtonsVisible(true);
        
        ressortCombo.setItems(ressortService.findAll());
        ressortCombo.setItemLabelGenerator(Ressort::getName);
        ressortCombo.setRequired(true);
        
        schichtCombo.setItems(schichtService.findAll());
        schichtCombo.setItemLabelGenerator(Schicht::getName);
        
        statusCombo.setItems(Einsatz.EinsatzStatus.values());
        statusCombo.setValue(Einsatz.EinsatzStatus.OFFEN);
        
        binder.forField(beschreibungField)
            .asRequired("Beschreibung ist erforderlich")
            .bind(Einsatz::getBeschreibung, Einsatz::setBeschreibung);
        
        binder.forField(startzeitField)
            .asRequired("Startzeit ist erforderlich")
            .bind(Einsatz::getStartzeit, Einsatz::setStartzeit);
        
        binder.forField(endzeitField)
            .asRequired("Endzeit ist erforderlich")
            .bind(Einsatz::getEndzeit, Einsatz::setEndzeit);
        
        binder.forField(ortField)
            .bind(Einsatz::getOrt, Einsatz::setOrt);
        
        binder.forField(mittelField)
            .bind(Einsatz::getMittel, Einsatz::setMittel);
        
        binder.forField(benoetigteHelferField)
            .bind(Einsatz::getBenoetigteHelfer, Einsatz::setBenoetigteHelfer);
        
        binder.forField(ressortCombo)
            .asRequired("Ressort ist erforderlich")
            .bind(Einsatz::getRessort, Einsatz::setRessort);
        
        binder.forField(schichtCombo)
            .bind(Einsatz::getSchicht, Einsatz::setSchicht);
        
        binder.forField(statusCombo)
            .bind(Einsatz::getStatus, Einsatz::setStatus);
        
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(event -> saveEinsatz());
        
        cancelButton.addClickListener(event -> closeEditor());
        
        newButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        newButton.addClickListener(event -> addEinsatz());
    }
    
    private void configureHelferAssignment() {
        helferCombo.setItems(helferService.findAll());
        helferCombo.setItemLabelGenerator(h -> h.getVorname() + " " + h.getNachname());
        helferCombo.setWidthFull();
        
        assignButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        assignButton.addClickListener(event -> assignHelfer());
        
        assignedHelferGrid.addColumn(h -> h.getVorname() + " " + h.getNachname())
            .setHeader("Zugewiesene Helfer").setAutoWidth(true);
        assignedHelferGrid.addColumn(
            new ComponentRenderer<>(helfer -> {
                Button removeButton = new Button("Entfernen");
                removeButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
                removeButton.addClickListener(e -> removeHelfer(helfer));
                return removeButton;
            })
        ).setHeader("Aktion").setAutoWidth(true);
        assignedHelferGrid.setHeight("200px");
    }
    
    private VerticalLayout createFormLayout() {
        VerticalLayout layout = new VerticalLayout();
        layout.add(
            beschreibungField,
            new HorizontalLayout(startzeitField, endzeitField),
            new HorizontalLayout(ortField, mittelField),
            new HorizontalLayout(ressortCombo, schichtCombo),
            new HorizontalLayout(benoetigteHelferField, statusCombo),
            new HorizontalLayout(saveButton, cancelButton)
        );
        
        // Helper assignment section
        VerticalLayout helferSection = new VerticalLayout();
        helferSection.add(
            new H3("Helfer zuweisen"),
            new HorizontalLayout(helferCombo, assignButton),
            assignedHelferGrid
        );
        helferSection.setVisible(false);
        helferSection.setId("helfer-section");
        
        layout.add(helferSection);
        layout.setVisible(false);
        return layout;
    }
    
    private void addEinsatz() {
        grid.asSingleSelect().clear();
        editEinsatz(new Einsatz());
    }
    
    private void editEinsatz(Einsatz einsatz) {
        if (einsatz == null) {
            closeEditor();
        } else {
            currentEinsatz = einsatz;
            binder.readBean(einsatz);
            formLayout.setVisible(true);
            
            // Show helper assignment only for saved assignments
            formLayout.getChildren()
                .filter(c -> "helfer-section".equals(c.getId().orElse("")))
                .findFirst()
                .ifPresent(c -> c.setVisible(einsatz.getId() != null));
            
            if (einsatz.getId() != null) {
                updateAssignedHelferGrid();
            }
        }
    }
    
    private void closeEditor() {
        currentEinsatz = null;
        binder.readBean(null);
        formLayout.setVisible(false);
        grid.asSingleSelect().clear();
    }
    
    private void saveEinsatz() {
        try {
            if (currentEinsatz == null) {
                currentEinsatz = new Einsatz();
            }
            binder.writeBean(currentEinsatz);
            currentEinsatz = einsatzService.save(currentEinsatz);
            updateList();
            
            // Show helper assignment section after first save
            formLayout.getChildren()
                .filter(c -> "helfer-section".equals(c.getId().orElse("")))
                .findFirst()
                .ifPresent(c -> c.setVisible(true));
            
            Notification notification = Notification.show("Einsatz erfolgreich gespeichert");
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (ValidationException e) {
            Notification notification = Notification.show("Bitte füllen Sie alle erforderlichen Felder aus");
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (IllegalArgumentException e) {
            Notification notification = Notification.show(e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (Exception e) {
            log.error("Error saving einsatz", e);
            Notification notification = Notification.show("Fehler beim Speichern: " + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
    
    private void assignHelfer() {
        Helfer helfer = helferCombo.getValue();
        if (helfer == null) {
            Notification.show("Bitte wählen Sie einen Helfer aus");
            return;
        }
        
        if (currentEinsatz == null || currentEinsatz.getId() == null) {
            Notification.show("Bitte speichern Sie den Einsatz zuerst");
            return;
        }
        
        try {
            currentEinsatz = einsatzService.assignHelfer(currentEinsatz.getId(), helfer);
            updateAssignedHelferGrid();
            updateList();
            helferCombo.clear();
            
            Notification notification = Notification.show("Helfer erfolgreich zugewiesen");
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (IllegalStateException e) {
            Notification notification = Notification.show("Konflikt: " + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (Exception e) {
            log.error("Error assigning helfer", e);
            Notification notification = Notification.show("Fehler: " + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
    
    private void removeHelfer(Helfer helfer) {
        if (currentEinsatz == null || currentEinsatz.getId() == null) {
            return;
        }
        
        try {
            currentEinsatz = einsatzService.removeHelfer(currentEinsatz.getId(), helfer);
            updateAssignedHelferGrid();
            updateList();
            
            Notification notification = Notification.show("Helfer entfernt");
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (Exception e) {
            log.error("Error removing helfer", e);
            Notification notification = Notification.show("Fehler: " + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
    
    private void updateAssignedHelferGrid() {
        if (currentEinsatz != null) {
            assignedHelferGrid.setItems(currentEinsatz.getZugewieseneHelfer());
        }
    }
    
    private void updateList() {
        grid.setItems(einsatzService.findAll());
    }
}
