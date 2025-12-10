package ch.flossrennen.eventmanagementsystem.views;

import ch.flossrennen.eventmanagementsystem.model.Helfer;
import ch.flossrennen.eventmanagementsystem.model.Ressort;
import ch.flossrennen.eventmanagementsystem.service.HelferService;
import ch.flossrennen.eventmanagementsystem.service.RessortService;
import ch.flossrennen.eventmanagementsystem.views.components.Breadcrumbs;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
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
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;

@Route("helfer")
@Slf4j
public class HelferView extends VerticalLayout {

    private final HelferService helferService;
    private final RessortService ressortService;

    private final Grid<Helfer> grid = new Grid<>(Helfer.class, false);
    private final Binder<Helfer> binder = new Binder<>(Helfer.class);

    private final TextField vornameField = new TextField("Vorname");
    private final TextField nachnameField = new TextField("Nachname");
    private final EmailField emailField = new EmailField("Email");
    private final TextField telefonField = new TextField("Telefon");
    private final ComboBox<Ressort> ressortCombo = new ComboBox<>("Stammressort");

    private final Button saveButton = new Button("Speichern", VaadinIcon.CHECK.create());
    private final Button cancelButton = new Button("Abbrechen", VaadinIcon.CLOSE.create());
    private final Button deleteButton = new Button("Löschen", VaadinIcon.TRASH.create());
    private final Button newButton = new Button("Neuer Helfer", VaadinIcon.PLUS.create());

    private final TextField searchField = new TextField();
    private final ComboBox<Ressort> filterRessort = new ComboBox<>("Filter nach Ressort");

    private Helfer currentHelfer;
    private VerticalLayout formLayout;

    public HelferView(HelferService helferService, RessortService ressortService) {
        this.helferService = helferService;
        this.ressortService = ressortService;

        addClassNames(LumoUtility.Padding.MEDIUM);
        setSizeFull();

        Breadcrumbs breadcrumbs = Breadcrumbs.forHelfer();

        H1 title = new H1("Helferverwaltung");
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

        grid.addColumn(Helfer::getId).setHeader("ID").setWidth("80px").setFlexGrow(0);
        grid.addColumn(Helfer::getVorname).setHeader("Vorname").setAutoWidth(true);
        grid.addColumn(Helfer::getNachname).setHeader("Nachname").setAutoWidth(true);
        grid.addColumn(Helfer::getEmail).setHeader("Email").setAutoWidth(true);
        grid.addColumn(Helfer::getTelefon).setHeader("Telefon").setAutoWidth(true);
        grid.addColumn(h -> h.getRessort() != null ? h.getRessort().getName() : "-")
            .setHeader("Stammressort").setAutoWidth(true);

        grid.addColumn(new ComponentRenderer<>(helfer -> {
            Button editBtn = new Button(VaadinIcon.EDIT.create());
            editBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
            editBtn.addClickListener(e -> editHelfer(helfer));
            editBtn.getElement().setAttribute("aria-label", "Bearbeiten");

            Button deleteBtn = new Button(VaadinIcon.TRASH.create());
            deleteBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ERROR);
            deleteBtn.addClickListener(e -> confirmDelete(helfer));
            deleteBtn.getElement().setAttribute("aria-label", "Löschen");

            HorizontalLayout actions = new HorizontalLayout(editBtn, deleteBtn);
            actions.setSpacing(false);
            return actions;
        })).setHeader("Aktionen").setWidth("140px").setFlexGrow(0);
    }
    
    private void configureForm() {
        vornameField.setRequired(true);
        vornameField.setMaxLength(100);
        vornameField.setPrefixComponent(VaadinIcon.USER.create());

        nachnameField.setRequired(true);
        nachnameField.setMaxLength(100);
        nachnameField.setPrefixComponent(VaadinIcon.USER.create());

        emailField.setMaxLength(150);
        emailField.setClearButtonVisible(true);
        emailField.setPrefixComponent(VaadinIcon.ENVELOPE.create());

        telefonField.setMaxLength(20);
        telefonField.setPrefixComponent(VaadinIcon.PHONE.create());

        ressortCombo.setItems(ressortService.findAll());
        ressortCombo.setItemLabelGenerator(Ressort::getName);
        ressortCombo.setPrefixComponent(VaadinIcon.SITEMAP.create());

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

        binder.forField(ressortCombo)
            .bind(Helfer::getRessort, Helfer::setRessort);

        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(event -> saveHelfer());

        cancelButton.addClickListener(event -> closeEditor());

        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        deleteButton.addClickListener(event -> confirmDelete(currentHelfer));

        newButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        newButton.addClickListener(event -> addHelfer());
    }

    private void configureToolbar() {
        searchField.setPlaceholder("Suche nach Name oder Email...");
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.addValueChangeListener(e -> updateList());
        searchField.setWidth("300px");

        filterRessort.setItems(ressortService.findAll());
        filterRessort.setItemLabelGenerator(Ressort::getName);
        filterRessort.setPlaceholder("Alle Ressorts");
        filterRessort.setClearButtonVisible(true);
        filterRessort.addValueChangeListener(e -> updateList());
        filterRessort.setWidth("200px");
    }

    private HorizontalLayout createToolbar() {
        HorizontalLayout toolbar = new HorizontalLayout(searchField, filterRessort, newButton);
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

        HorizontalLayout nameRow = new HorizontalLayout(vornameField, nachnameField);
        nameRow.setWidthFull();
        vornameField.setWidthFull();
        nachnameField.setWidthFull();

        HorizontalLayout contactRow = new HorizontalLayout(emailField, telefonField);
        contactRow.setWidthFull();
        emailField.setWidthFull();
        telefonField.setWidthFull();

        ressortCombo.setWidthFull();

        HorizontalLayout buttonLayout = new HorizontalLayout(saveButton, cancelButton, deleteButton);
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttonLayout.setWidthFull();

        layout.add(nameRow, contactRow, ressortCombo, buttonLayout);
        return layout;
    }

    private void addHelfer() {
        editHelfer(new Helfer());
    }

    private void editHelfer(Helfer helfer) {
        if (helfer == null) {
            closeEditor();
        } else {
            currentHelfer = helfer;
            binder.readBean(helfer);
            formLayout.setVisible(true);
            deleteButton.setVisible(helfer.getId() != null);
        }
    }

    private void closeEditor() {
        currentHelfer = null;
        binder.readBean(null);
        formLayout.setVisible(false);
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

            Notification notification = Notification.show("Helfer erfolgreich gespeichert",
                3000, Notification.Position.TOP_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (ValidationException e) {
            Notification notification = Notification.show("Bitte füllen Sie alle erforderlichen Felder aus",
                3000, Notification.Position.TOP_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (DataIntegrityViolationException e) {
            Notification notification = Notification.show("Diese E-Mail-Adresse wird bereits verwendet",
                3000, Notification.Position.TOP_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void confirmDelete(Helfer helfer) {
        if (helfer == null || helfer.getId() == null) {
            return;
        }

        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Helfer löschen?");
        dialog.setText("Möchten Sie " + helfer.getVorname() + " " + helfer.getNachname() + " wirklich löschen?");
        dialog.setCancelable(true);
        dialog.setCancelText("Abbrechen");
        dialog.setConfirmText("Löschen");
        dialog.setConfirmButtonTheme("error primary");

        dialog.addConfirmListener(event -> deleteHelfer(helfer));
        dialog.open();
    }

    private void deleteHelfer(Helfer helfer) {
        try {
            helferService.delete(helfer.getId());
            updateList();
            closeEditor();

            Notification notification = Notification.show("Helfer erfolgreich gelöscht",
                3000, Notification.Position.TOP_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (Exception e) {
            log.error("Error deleting helfer", e);
            Notification notification = Notification.show("Fehler beim Löschen: " + e.getMessage(),
                3000, Notification.Position.TOP_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void updateList() {
        var helfers = helferService.findAll().stream()
            .filter(h -> {
                String search = searchField.getValue();
                if (search != null && !search.isBlank()) {
                    String searchLower = search.toLowerCase();
                    boolean matchesName = (h.getVorname() + " " + h.getNachname()).toLowerCase().contains(searchLower);
                    boolean matchesEmail = h.getEmail() != null && h.getEmail().toLowerCase().contains(searchLower);
                    if (!matchesName && !matchesEmail) {
                        return false;
                    }
                }

                Ressort filterRes = filterRessort.getValue();
                if (filterRes != null && !filterRes.equals(h.getRessort())) {
                    return false;
                }

                return true;
            })
            .toList();

        grid.setItems(helfers);
    }
}
