package ch.flossrennen.eventmanagementsystem.views;

import ch.flossrennen.eventmanagementsystem.model.Ressort;
import ch.flossrennen.eventmanagementsystem.service.RessortService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
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

@Route("ressorts")
@Slf4j
public class RessortView extends VerticalLayout {

    private final RessortService ressortService;
    
    private final Grid<Ressort> grid = new Grid<>(Ressort.class, false);
    private final Binder<Ressort> binder = new Binder<>(Ressort.class);
    
    private final TextField nameField = new TextField("Name");
    private final TextArea beschreibungField = new TextArea("Beschreibung");
    private final TextArea zustaendigkeitenField = new TextArea("Zuständigkeiten");
    private final TextField kontaktpersonField = new TextField("Kontaktperson");
    
    private final Button saveButton = new Button("Speichern");
    private final Button cancelButton = new Button("Abbrechen");
    private final Button newButton = new Button("Neues Ressort");
    
    private Ressort currentRessort;

    public RessortView(RessortService ressortService) {
        this.ressortService = ressortService;
        
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
        grid.addColumn(Ressort::getId).setHeader("ID").setAutoWidth(true);
        grid.addColumn(Ressort::getName).setHeader("Name").setAutoWidth(true);
        grid.addColumn(Ressort::getBeschreibung).setHeader("Beschreibung").setAutoWidth(true);
        grid.addColumn(Ressort::getZustaendigkeiten).setHeader("Zuständigkeiten").setAutoWidth(true);
        grid.addColumn(Ressort::getKontaktperson).setHeader("Kontaktperson").setAutoWidth(true);
        
        grid.asSingleSelect().addValueChangeListener(event -> editRessort(event.getValue()));
    }
    
    private void configureForm() {
        nameField.setRequired(true);
        nameField.setMaxLength(100);
        
        beschreibungField.setMaxLength(500);
        beschreibungField.setHeight("100px");
        
        zustaendigkeitenField.setMaxLength(300);
        zustaendigkeitenField.setHeight("80px");
        
        kontaktpersonField.setMaxLength(100);
        
        binder.forField(nameField)
            .asRequired("Name ist erforderlich")
            .bind(Ressort::getName, Ressort::setName);
        
        binder.forField(beschreibungField)
            .bind(Ressort::getBeschreibung, Ressort::setBeschreibung);
        
        binder.forField(zustaendigkeitenField)
            .bind(Ressort::getZustaendigkeiten, Ressort::setZustaendigkeiten);
        
        binder.forField(kontaktpersonField)
            .bind(Ressort::getKontaktperson, Ressort::setKontaktperson);
        
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(event -> saveRessort());
        
        cancelButton.addClickListener(event -> closeEditor());
        
        newButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        newButton.addClickListener(event -> addRessort());
    }
    
    private VerticalLayout createFormLayout() {
        VerticalLayout formLayout = new VerticalLayout(
            nameField,
            beschreibungField,
            zustaendigkeitenField,
            kontaktpersonField,
            new HorizontalLayout(saveButton, cancelButton)
        );
        formLayout.setVisible(false);
        return formLayout;
    }
    
    private void addRessort() {
        grid.asSingleSelect().clear();
        editRessort(new Ressort());
    }
    
    private void editRessort(Ressort ressort) {
        if (ressort == null) {
            closeEditor();
        } else {
            currentRessort = ressort;
            binder.readBean(ressort);
            getChildren().filter(c -> c instanceof VerticalLayout && c != grid)
                .findFirst().ifPresent(c -> c.setVisible(true));
        }
    }
    
    private void closeEditor() {
        currentRessort = null;
        binder.readBean(null);
        getChildren().filter(c -> c instanceof VerticalLayout && c != grid)
            .findFirst().ifPresent(c -> c.setVisible(false));
        grid.asSingleSelect().clear();
    }
    
    private void saveRessort() {
        try {
            if (currentRessort == null) {
                currentRessort = new Ressort();
            }
            binder.writeBean(currentRessort);
            ressortService.save(currentRessort);
            updateList();
            closeEditor();
            
            Notification notification = Notification.show("Ressort erfolgreich gespeichert");
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (ValidationException e) {
            Notification notification = Notification.show("Bitte füllen Sie alle erforderlichen Felder aus");
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (Exception e) {
            Notification notification = Notification.show("Fehler beim Speichern: " + e.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
    
    private void updateList() {
        grid.setItems(ressortService.findAll());
    }
}
