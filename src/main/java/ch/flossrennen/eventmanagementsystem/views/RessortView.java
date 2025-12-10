package ch.flossrennen.eventmanagementsystem.views;

import ch.flossrennen.eventmanagementsystem.model.Ressort;
import ch.flossrennen.eventmanagementsystem.service.RessortService;
import ch.flossrennen.eventmanagementsystem.views.components.Breadcrumbs;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
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
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
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

    private final Button saveButton = new Button("Speichern", VaadinIcon.CHECK.create());
    private final Button cancelButton = new Button("Abbrechen", VaadinIcon.CLOSE.create());
    private final Button deleteButton = new Button("Löschen", VaadinIcon.TRASH.create());
    private final Button newButton = new Button("Neues Ressort", VaadinIcon.PLUS.create());

    private final TextField searchField = new TextField();

    private Ressort currentRessort;
    private VerticalLayout formLayout;

    public RessortView(RessortService ressortService) {
        this.ressortService = ressortService;

        addClassNames(LumoUtility.Padding.MEDIUM);
        setSizeFull();

        Breadcrumbs breadcrumbs = Breadcrumbs.forRessort();

        H1 title = new H1("Ressortverwaltung");
        title.addClassNames(LumoUtility.Margin.Bottom.MEDIUM, LumoUtility.Margin.Top.NONE);

        configureGrid();
        configureForm();
        configureToolbar();

        formLayout = createFormLayout();

        HorizontalLayout toolbar = createToolbar();
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

        grid.addColumn(Ressort::getId).setHeader("ID").setWidth("80px").setFlexGrow(0);
        grid.addColumn(Ressort::getName).setHeader("Name").setAutoWidth(true);
        grid.addColumn(Ressort::getBeschreibung).setHeader("Beschreibung").setAutoWidth(true);
        grid.addColumn(Ressort::getZustaendigkeiten).setHeader("Zuständigkeiten").setAutoWidth(true);
        grid.addColumn(Ressort::getKontaktperson).setHeader("Kontaktperson").setAutoWidth(true);

        grid.addColumn(new ComponentRenderer<>(ressort -> {
            Button editBtn = new Button(VaadinIcon.EDIT.create());
            editBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
            editBtn.addClickListener(e -> editRessort(ressort));
            editBtn.getElement().setAttribute("aria-label", "Bearbeiten");

            Button deleteBtn = new Button(VaadinIcon.TRASH.create());
            deleteBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
            deleteBtn.addClickListener(e -> confirmDelete(ressort));
            deleteBtn.getElement().setAttribute("aria-label", "Löschen");

            HorizontalLayout actions = new HorizontalLayout(editBtn, deleteBtn);
            actions.setSpacing(false);
            return actions;
        })).setHeader("Aktionen").setWidth("140px").setFlexGrow(0);
    }

    private void configureForm() {
        nameField.setRequired(true);
        nameField.setMaxLength(100);
        nameField.setPrefixComponent(VaadinIcon.TAG.create());

        beschreibungField.setMaxLength(500);
        beschreibungField.setHeight("100px");

        zustaendigkeitenField.setMaxLength(300);
        zustaendigkeitenField.setHeight("80px");

        kontaktpersonField.setMaxLength(100);
        kontaktpersonField.setPrefixComponent(VaadinIcon.USER.create());

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

        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        deleteButton.addClickListener(event -> confirmDelete(currentRessort));

        newButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        newButton.addClickListener(event -> addRessort());
    }

    private void configureToolbar() {
        searchField.setPlaceholder("Suche nach Ressort...");
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.addValueChangeListener(e -> updateList());
        searchField.setWidth("300px");
    }

    private HorizontalLayout createToolbar() {
        HorizontalLayout toolbar = new HorizontalLayout(searchField, newButton);
        toolbar.setWidthFull();
        toolbar.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        toolbar.setAlignItems(FlexComponent.Alignment.CENTER);
        toolbar.addClassNames(LumoUtility.Gap.SMALL);
        return toolbar;
    }

    private VerticalLayout createFormLayout() {
        VerticalLayout layout = new VerticalLayout();
        layout.addClassNames(LumoUtility.Border.ALL, LumoUtility.BorderColor.CONTRAST_10,
                            LumoUtility.BorderRadius.MEDIUM, LumoUtility.Padding.MEDIUM,
                            LumoUtility.Background.BASE, LumoUtility.Margin.Bottom.MEDIUM);
        layout.setSpacing(true);
        layout.setVisible(false);

        nameField.setWidthFull();
        beschreibungField.setWidthFull();
        zustaendigkeitenField.setWidthFull();
        kontaktpersonField.setWidthFull();

        HorizontalLayout buttonLayout = new HorizontalLayout(saveButton, cancelButton, deleteButton);
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttonLayout.setWidthFull();

        layout.add(nameField, beschreibungField, zustaendigkeitenField, kontaktpersonField, buttonLayout);
        return layout;
    }

    private void addRessort() {
        editRessort(new Ressort());
    }

    private void editRessort(Ressort ressort) {
        if (ressort == null) {
            closeEditor();
        } else {
            currentRessort = ressort;
            binder.readBean(ressort);
            formLayout.setVisible(true);
            deleteButton.setVisible(ressort.getId() != null);
        }
    }

    private void closeEditor() {
        currentRessort = null;
        binder.readBean(null);
        formLayout.setVisible(false);
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

            Notification notification = Notification.show("Ressort erfolgreich gespeichert",
                3000, Notification.Position.TOP_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (ValidationException e) {
            Notification notification = Notification.show("Bitte füllen Sie alle erforderlichen Felder aus",
                3000, Notification.Position.TOP_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (Exception e) {
            log.error("Error saving ressort", e);
            Notification notification = Notification.show("Fehler beim Speichern: " + e.getMessage(),
                3000, Notification.Position.TOP_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void confirmDelete(Ressort ressort) {
        if (ressort == null || ressort.getId() == null) {
            return;
        }

        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Ressort löschen?");
        dialog.setText("Möchten Sie das Ressort \"" + ressort.getName() + "\" wirklich löschen?");
        dialog.setCancelable(true);
        dialog.setCancelText("Abbrechen");
        dialog.setConfirmText("Löschen");
        dialog.setConfirmButtonTheme("error primary");

        dialog.addConfirmListener(event -> deleteRessort(ressort));
        dialog.open();
    }

    private void deleteRessort(Ressort ressort) {
        try {
            ressortService.delete(ressort.getId());
            updateList();
            closeEditor();

            Notification notification = Notification.show("Ressort erfolgreich gelöscht",
                3000, Notification.Position.TOP_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (Exception e) {
            log.error("Error deleting ressort", e);
            Notification notification = Notification.show("Fehler beim Löschen: " + e.getMessage(),
                3000, Notification.Position.TOP_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void updateList() {
        var ressorts = ressortService.findAll().stream()
            .filter(r -> {
                String search = searchField.getValue();
                if (search != null && !search.isBlank()) {
                    String searchLower = search.toLowerCase();
                    boolean matchesName = r.getName() != null && r.getName().toLowerCase().contains(searchLower);
                    boolean matchesDesc = r.getBeschreibung() != null && r.getBeschreibung().toLowerCase().contains(searchLower);
                    return matchesName || matchesDesc;
                }
                return true;
            })
            .toList();

        grid.setItems(ressorts);
    }
}
