package ch.flossrennen.eventmanagementsystem.views;

import ch.flossrennen.eventmanagementsystem.model.Einsatz;
import ch.flossrennen.eventmanagementsystem.model.Helfer;
import ch.flossrennen.eventmanagementsystem.model.Ressort;
import ch.flossrennen.eventmanagementsystem.model.Schicht;
import ch.flossrennen.eventmanagementsystem.service.EinsatzService;
import ch.flossrennen.eventmanagementsystem.service.HelferService;
import ch.flossrennen.eventmanagementsystem.service.RessortService;
import ch.flossrennen.eventmanagementsystem.service.SchichtService;
import ch.flossrennen.eventmanagementsystem.views.components.Breadcrumbs;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
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

    private final Button saveButton = new Button("Speichern", VaadinIcon.CHECK.create());
    private final Button cancelButton = new Button("Abbrechen", VaadinIcon.CLOSE.create());
    private final Button deleteButton = new Button("Löschen", VaadinIcon.TRASH.create());
    private final Button newButton = new Button("Neuer Einsatz", VaadinIcon.PLUS.create());

    // Helper assignment components
    private final ComboBox<Helfer> helferCombo = new ComboBox<>("Helfer zuweisen");
    private final Button assignButton = new Button("Zuweisen", VaadinIcon.PLUS.create());
    private final Grid<Helfer> assignedHelferGrid = new Grid<>(Helfer.class, false);

    private Einsatz currentEinsatz;
    private VerticalLayout formLayout;

    public EinsatzView(EinsatzService einsatzService, RessortService ressortService,
                      SchichtService schichtService, HelferService helferService) {
        this.einsatzService = einsatzService;
        this.ressortService = ressortService;
        this.schichtService = schichtService;
        this.helferService = helferService;

        addClassNames(LumoUtility.Padding.MEDIUM);
        setSizeFull();

        Breadcrumbs breadcrumbs = Breadcrumbs.forEinsatz();

        H1 title = new H1("Einsatzverwaltung");
        title.addClassNames(LumoUtility.Margin.Bottom.MEDIUM, LumoUtility.Margin.Top.NONE);

        configureGrid();
        configureForm();
        configureHelferAssignment();

        formLayout = createFormLayout();

        HorizontalLayout toolbar = new HorizontalLayout(newButton);
        toolbar.addClassNames(LumoUtility.Margin.Bottom.SMALL);

        Div gridWrapper = new Div(grid);
        gridWrapper.addClassNames(LumoUtility.Border.ALL, LumoUtility.BorderColor.CONTRAST_10,
                                  LumoUtility.BorderRadius.MEDIUM, LumoUtility.Padding.SMALL);
        gridWrapper.setSizeFull();

        add(breadcrumbs, title, toolbar, formLayout, gridWrapper);
        setFlexGrow(1, gridWrapper);

        updateList();
        closeEditor();
    }
    
    private void configureGrid() {
        grid.addClassNames(LumoUtility.Border.NONE);
        grid.setAllRowsVisible(true);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm", Locale.GERMAN);

        grid.addColumn(Einsatz::getId).setHeader("ID").setWidth("70px").setFlexGrow(0);
        grid.addColumn(Einsatz::getBeschreibung).setHeader("Beschreibung").setAutoWidth(true);
        grid.addColumn(e -> e.getStartzeit() != null ? e.getStartzeit().format(formatter) : "-")
            .setHeader("Startzeit").setWidth("140px").setFlexGrow(0);
        grid.addColumn(e -> e.getRessort() != null ? e.getRessort().getName() : "-")
            .setHeader("Ressort").setAutoWidth(true);
        grid.addColumn(e -> e.getZugewieseneHelfer().size() + "/" + e.getBenoetigteHelfer())
            .setHeader("Helfer").setWidth("90px").setFlexGrow(0);
        grid.addColumn(new ComponentRenderer<>(einsatz -> {
            Div badge = new Div();
            badge.setText(einsatz.getStatus().toString());
            badge.getStyle()
                .set("padding", "4px 8px")
                .set("border-radius", "4px")
                .set("font-size", "0.85em")
                .set("font-weight", "500");

            switch (einsatz.getStatus()) {
                case OFFEN -> badge.getStyle()
                    .set("background", "var(--lumo-error-color-10pct)")
                    .set("color", "var(--lumo-error-color)");
                case IN_PLANUNG -> badge.getStyle()
                    .set("background", "var(--lumo-warning-color-10pct)")
                    .set("color", "var(--lumo-warning-color)");
                case VOLLSTAENDIG -> badge.getStyle()
                    .set("background", "var(--lumo-success-color-10pct)")
                    .set("color", "var(--lumo-success-color)");
                case ABGESCHLOSSEN -> badge.getStyle()
                    .set("background", "var(--lumo-contrast-10pct)")
                    .set("color", "var(--lumo-contrast-90pct)");
            }
            return badge;
        })).setHeader("Status").setWidth("130px").setFlexGrow(0);

        grid.addColumn(new ComponentRenderer<>(einsatz -> {
            Button editBtn = new Button(VaadinIcon.EDIT.create());
            editBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
            editBtn.addClickListener(e -> editEinsatz(einsatz));
            editBtn.getElement().setAttribute("aria-label", "Bearbeiten");

            Button deleteBtn = new Button(VaadinIcon.TRASH.create());
            deleteBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
            deleteBtn.addClickListener(e -> confirmDelete(einsatz));
            deleteBtn.getElement().setAttribute("aria-label", "Löschen");

            HorizontalLayout actions = new HorizontalLayout(editBtn, deleteBtn);
            actions.setSpacing(false);
            return actions;
        })).setHeader("Aktionen").setWidth("140px").setFlexGrow(0);
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

        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        deleteButton.addClickListener(event -> confirmDelete(currentEinsatz));

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
        layout.addClassNames(LumoUtility.Border.ALL, LumoUtility.BorderColor.CONTRAST_10,
                            LumoUtility.BorderRadius.MEDIUM, LumoUtility.Padding.MEDIUM,
                            LumoUtility.Background.BASE, LumoUtility.Margin.Bottom.MEDIUM);
        layout.setSpacing(true);
        layout.setVisible(false);

        beschreibungField.setWidthFull();

        HorizontalLayout timeRow = new HorizontalLayout(startzeitField, endzeitField);
        timeRow.setWidthFull();
        startzeitField.setWidthFull();
        endzeitField.setWidthFull();

        HorizontalLayout locationRow = new HorizontalLayout(ortField, mittelField);
        locationRow.setWidthFull();
        ortField.setWidthFull();
        mittelField.setWidthFull();

        HorizontalLayout ressortRow = new HorizontalLayout(ressortCombo, schichtCombo);
        ressortRow.setWidthFull();
        ressortCombo.setWidthFull();
        schichtCombo.setWidthFull();

        HorizontalLayout helferStatusRow = new HorizontalLayout(benoetigteHelferField, statusCombo);
        helferStatusRow.setWidthFull();
        benoetigteHelferField.setWidth("150px");
        statusCombo.setWidthFull();

        HorizontalLayout buttonLayout = new HorizontalLayout(saveButton, cancelButton, deleteButton);
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttonLayout.setWidthFull();

        layout.add(beschreibungField, timeRow, locationRow, ressortRow, helferStatusRow, buttonLayout);

        // Helper assignment section
        VerticalLayout helferSection = new VerticalLayout();
        helferSection.addClassNames(LumoUtility.Border.TOP, LumoUtility.BorderColor.CONTRAST_10,
                                    LumoUtility.Padding.Top.MEDIUM, LumoUtility.Margin.Top.MEDIUM);
        helferSection.setSpacing(true);

        H3 helferTitle = new H3("Helfer zuweisen");
        helferTitle.getStyle().set("margin-top", "0");

        HorizontalLayout helferAssignRow = new HorizontalLayout(helferCombo, assignButton);
        helferAssignRow.setWidthFull();
        helferAssignRow.setAlignItems(FlexComponent.Alignment.END);
        helferCombo.setWidthFull();

        helferSection.add(helferTitle, helferAssignRow, assignedHelferGrid);
        helferSection.setVisible(false);
        helferSection.setId("helfer-section");

        layout.add(helferSection);
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
            deleteButton.setVisible(einsatz.getId() != null);

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

    private void confirmDelete(Einsatz einsatz) {
        if (einsatz == null || einsatz.getId() == null) {
            return;
        }

        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Einsatz löschen?");
        dialog.setText("Möchten Sie den Einsatz \"" + einsatz.getBeschreibung() + "\" wirklich löschen?");
        dialog.setCancelable(true);
        dialog.setCancelText("Abbrechen");
        dialog.setConfirmText("Löschen");
        dialog.setConfirmButtonTheme("error primary");

        dialog.addConfirmListener(event -> deleteEinsatz(einsatz));
        dialog.open();
    }

    private void deleteEinsatz(Einsatz einsatz) {
        try {
            einsatzService.delete(einsatz.getId());
            updateList();
            closeEditor();

            Notification notification = Notification.show("Einsatz erfolgreich gelöscht",
                3000, Notification.Position.TOP_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (Exception e) {
            log.error("Error deleting einsatz", e);
            Notification notification = Notification.show("Fehler beim Löschen: " + e.getMessage(),
                3000, Notification.Position.TOP_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void updateList() {
        grid.setItems(einsatzService.findAll());
    }
}
