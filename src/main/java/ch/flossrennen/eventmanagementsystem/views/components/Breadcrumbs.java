package ch.flossrennen.eventmanagementsystem.views.components;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.ArrayList;
import java.util.List;

/**
 * Breadcrumb navigation component for easy navigation between views.
 * Displays the navigation path from Home to the current view.
 */
public class Breadcrumbs extends Div {

    private final HorizontalLayout layout;
    private final List<BreadcrumbItem> items;

    public Breadcrumbs() {
        this.layout = new HorizontalLayout();
        this.items = new ArrayList<>();

        layout.setSpacing(false);
        layout.addClassNames(LumoUtility.Gap.XSMALL, LumoUtility.AlignItems.CENTER);

        addClassNames(LumoUtility.Padding.Vertical.SMALL,
                     LumoUtility.Padding.Horizontal.NONE,
                     LumoUtility.Margin.Bottom.SMALL);

        getStyle()
            .set("font-size", "0.875rem")
            .set("color", "var(--lumo-secondary-text-color)");

        add(layout);

        // Always add Home as first breadcrumb (empty string "" navigates to root)
        addItem("Home", "", true);
    }

    /**
     * Adds a breadcrumb item.
     *
     * @param label The display text
     * @param route The navigation route (empty string "" for home/root)
     * @param isClickable Whether the item should be clickable
     */
    public void addItem(String label, String route, boolean isClickable) {
        // Add separator if not the first item
        if (!items.isEmpty()) {
            Span separator = new Span(VaadinIcon.ANGLE_RIGHT.create());
            separator.addClassNames(LumoUtility.TextColor.TERTIARY);
            separator.getStyle().set("font-size", "0.75rem");
            layout.add(separator);
        }

        BreadcrumbItem item = new BreadcrumbItem(label, route, isClickable);
        items.add(item);

        if (isClickable) {
            Anchor link = new Anchor(route, label);
            link.getStyle()
                .set("text-decoration", "none")
                .set("color", "var(--lumo-primary-text-color)")
                .set("transition", "color 0.2s");
            link.getElement().setAttribute("onmouseover", "this.style.color='var(--lumo-primary-color)'");
            link.getElement().setAttribute("onmouseout", "this.style.color='var(--lumo-primary-text-color)'");
            layout.add(link);
        } else {
            Span current = new Span(label);
            current.addClassNames(LumoUtility.FontWeight.SEMIBOLD);
            current.getStyle().set("color", "var(--lumo-body-text-color)");
            layout.add(current);
        }
    }

    /**
     * Creates a breadcrumb for the Dashboard view.
     */
    public static Breadcrumbs forDashboard() {
        Breadcrumbs breadcrumbs = new Breadcrumbs();
        breadcrumbs.addItem("Dashboard", "dashboard", false);
        return breadcrumbs;
    }

    /**
     * Creates a breadcrumb for the Helfer (Volunteers) view.
     */
    public static Breadcrumbs forHelfer() {
        Breadcrumbs breadcrumbs = new Breadcrumbs();
        breadcrumbs.addItem("Helferverwaltung", "helfer", false);
        return breadcrumbs;
    }

    /**
     * Creates a breadcrumb for the Ressort (Department) view.
     */
    public static Breadcrumbs forRessort() {
        Breadcrumbs breadcrumbs = new Breadcrumbs();
        breadcrumbs.addItem("Ressortverwaltung", "ressorts", false);
        return breadcrumbs;
    }

    /**
     * Creates a breadcrumb for the Einsatz (Assignment) view.
     */
    public static Breadcrumbs forEinsatz() {
        Breadcrumbs breadcrumbs = new Breadcrumbs();
        breadcrumbs.addItem("Einsatzplanung", "einsaetze", false);
        return breadcrumbs;
    }

    /**
     * Creates a breadcrumb for the Schicht (Shift) view.
     */
    public static Breadcrumbs forSchicht() {
        Breadcrumbs breadcrumbs = new Breadcrumbs();
        breadcrumbs.addItem("Schichtverwaltung", "schichten", false);
        return breadcrumbs;
    }

    /**
     * Internal class to represent a breadcrumb item.
     */
    private static class BreadcrumbItem {
        private final String label;
        private final String route;
        private final boolean isClickable;

        public BreadcrumbItem(String label, String route, boolean isClickable) {
            this.label = label;
            this.route = route;
            this.isClickable = isClickable;
        }
    }
}
