package ch.flossrennen.eventmanagementsystem.views;

import ch.flossrennen.eventmanagementsystem.model.Schicht;
import ch.flossrennen.eventmanagementsystem.service.SchichtService;
import ch.flossrennen.eventmanagementsystem.views.components.Breadcrumbs;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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

    private final Button saveButton = new Button("Speichern", VaadinIcon.CHECK.create());
    private final Button cancelButton = new Button("Abbrechen", VaadinIcon.CLOSE.create());
    private final Button deleteButton = new Button("Löschen", VaadinIcon.TRASH.create());
    private final Button newButton = new Button("Neue Schicht", VaadinIcon.PLUS.create());

    private Schicht currentSchicht;
    private VerticalLayout formLayout;

    public SchichtView(SchichtService schichtService) {
        this.schichtService = schichtService;

        addClassNames(LumoUtility.Padding.MEDIUM);
        setSizeFull();

        Breadcrumbs breadcrumbs = Breadcrumbs.forSchicht();

        H1 title = new H1("Schichtverwaltung");
        title.addClassNames(LumoUtility.Margin.Bottom.MEDIUM, LumoUtility.Margin.Top.NONE);

        configureGrid();
        configureForm();

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

        grid.addColumn(Schicht::getId).setHeader("ID").setWidth("80px").setFlexGrow(0);
        grid.addColumn(Schicht::getName).setHeader("Name").setAutoWidth(true);
        grid.addColumn(s -> s.getStartzeit() != null ? s.getStartzeit().format(formatter) : "-")
            .setHeader("Startzeit").setAutoWidth(true);
        grid.addColumn(s -> s.getEndzeit() != null ? s.getEndzeit().format(formatter) : "-")
            .setHeader("Endzeit").setAutoWidth(true);
        grid.addColumn(Schicht::getBeschreibung).setHeader("Beschreibung").setAutoWidth(true);

        grid.addColumn(new ComponentRenderer<>(schicht -> {
            Button editBtn = new Button(VaadinIcon.EDIT.create());
            editBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
            editBtn.addClickListener(e -> editSchicht(schicht));
            editBtn.getElement().setAttribute("aria-label", "Bearbeiten");

            Button deleteBtn = new Button(VaadinIcon.TRASH.create());
            deleteBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
            deleteBtn.addClickListener(e -> confirmDelete(schicht));
            deleteBtn.getElement().setAttribute("aria-label", "Löschen");

            HorizontalLayout actions = new HorizontalLayout(editBtn, deleteBtn);
            actions.setSpacing(false);
            return actions;
        })).setHeader("Aktionen").setWidth("140px").setFlexGrow(0);
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

        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        deleteButton.addClickListener(event -> confirmDelete(currentSchicht));

        newButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        newButton.addClickListener(event -> addSchicht());
    }

    private VerticalLayout createFormLayout() {
        VerticalLayout layout = new VerticalLayout();
        layout.addClassNames(LumoUtility.Border.ALL, LumoUtility.BorderColor.CONTRAST_10,
                            LumoUtility.BorderRadius.MEDIUM, LumoUtility.Padding.MEDIUM,
                            LumoUtility.Background.BASE, LumoUtility.Margin.Bottom.MEDIUM);
        layout.setSpacing(true);
        layout.setVisible(false);

        nameField.setWidthFull();

        HorizontalLayout timeRow = new HorizontalLayout(startzeitField, endzeitField);
        timeRow.setWidthFull();
        startzeitField.setWidthFull();
        endzeitField.setWidthFull();

        beschreibungField.setWidthFull();

        HorizontalLayout buttonLayout = new HorizontalLayout(saveButton, cancelButton, deleteButton);
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttonLayout.setWidthFull();

        layout.add(nameField, timeRow, beschreibungField, buttonLayout);
        return layout;
    }

    private void addSchicht() {
        editSchicht(new Schicht());
    }

    private void editSchicht(Schicht schicht) {
        if (schicht == null) {
            closeEditor();
        } else {
            currentSchicht = schicht;
            binder.readBean(schicht);
            formLayout.setVisible(true);
            deleteButton.setVisible(schicht.getId() != null);
        }
    }

    private void closeEditor() {
        currentSchicht = null;
        binder.readBean(null);
        formLayout.setVisible(false);
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

            Notification notification = Notification.show("Schicht erfolgreich gespeichert",
                3000, Notification.Position.TOP_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (ValidationException e) {
            Notification notification = Notification.show("Bitte füllen Sie alle erforderlichen Felder aus",
                3000, Notification.Position.TOP_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (IllegalArgumentException e) {
            Notification notification = Notification.show(e.getMessage(),
                3000, Notification.Position.TOP_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (Exception e) {
            log.error("Error saving schicht", e);
            Notification notification = Notification.show("Fehler beim Speichern: " + e.getMessage(),
                3000, Notification.Position.TOP_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void confirmDelete(Schicht schicht) {
        if (schicht == null || schicht.getId() == null) {
            return;
        }

        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Schicht löschen?");
        dialog.setText("Möchten Sie die Schicht \"" + schicht.getName() + "\" wirklich löschen?");
        dialog.setCancelable(true);
        dialog.setCancelText("Abbrechen");
        dialog.setConfirmText("Löschen");
        dialog.setConfirmButtonTheme("error primary");

        dialog.addConfirmListener(event -> deleteSchicht(schicht));
        dialog.open();
    }

    private void deleteSchicht(Schicht schicht) {
        try {
            schichtService.delete(schicht.getId());
            updateList();
            closeEditor();

            Notification notification = Notification.show("Schicht erfolgreich gelöscht",
                3000, Notification.Position.TOP_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (Exception e) {
            log.error("Error deleting schicht", e);
            Notification notification = Notification.show("Fehler beim Löschen: " + e.getMessage(),
                3000, Notification.Position.TOP_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void updateList() {
        grid.setItems(schichtService.findAll());
    }
}
