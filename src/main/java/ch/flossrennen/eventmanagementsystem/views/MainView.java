package ch.flossrennen.eventmanagementsystem.views;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

/**
 * Hauptansicht (Landing Page) der Applikation.
 * <p>
 * Diese View wird auf der Root-URL ("") angezeigt und dient als Einstiegspunkt.
 * Sie zeigt eine Übersicht aller verfügbaren Module und bietet Navigation zu:
 * <ul>
 * <li>Dashboard (Übersicht und Statistiken)</li>
 * <li>Ressortverwaltung (Verwaltung der Organisationsbereiche)</li>
 * <li>Helferverwaltung (Verwaltung der freiwilligen Helfer)</li>
 * <li>Schichtverwaltung (Zeitliche Einteilung der Veranstaltung)</li>
 * <li>Einsatzplanung (Zuweisung von Helfern zu Aufgaben)</li>
 * </ul>
 * <p>
 * Layout: Vertikal zentrierte Ansicht mit Titel, Untertitel und Navigationslinks
 */
@Route("")
public class MainView extends VerticalLayout {

    public MainView() {
        // Grundlegendes Layout: Abstände und zentrierte Ausrichtung
        setSpacing(true);
        setPadding(true);
        setAlignItems(Alignment.CENTER);

        // Haupttitel mit Primary-Farbe (Vaadin Lumo Theme)
        H1 title = new H1("Eventmanagementsystem Flossrennen");
        title.addClassNames(LumoUtility.TextColor.PRIMARY);

        // Beschreibender Untertitel
        Paragraph subtitle = new Paragraph("Von Handarbeit zu Klickarbeit - smarte Planung für das Flossrennen");

        // Überschrift für die Modulübersicht
        H2 modulesTitle = new H2("Module");

        // Container für die Navigationslinks (zentriert, ohne zusätzliche Abstände)
        VerticalLayout linksLayout = new VerticalLayout();
        linksLayout.setSpacing(false);
        linksLayout.setPadding(false);
        linksLayout.setAlignItems(Alignment.CENTER);

        // Dashboard-Link (hervorgehoben durch größere Schrift und fett)
        Anchor dashboardLink = new Anchor("dashboard", "Dashboard");
        dashboardLink.getStyle().set("font-size", "1.2em");
        dashboardLink.getStyle().set("margin", "5px");
        dashboardLink.getStyle().set("font-weight", "bold");

        // Navigationslinks zu den einzelnen Modulen
        Anchor ressortLink = new Anchor("ressorts", "Ressortverwaltung");
        ressortLink.getStyle().set("font-size", "1.1em");
        ressortLink.getStyle().set("margin", "5px");

        Anchor helferLink = new Anchor("helfer", "Helferverwaltung");
        helferLink.getStyle().set("font-size", "1.1em");
        helferLink.getStyle().set("margin", "5px");

        Anchor schichtLink = new Anchor("schichten", "Schichtverwaltung");
        schichtLink.getStyle().set("font-size", "1.1em");
        schichtLink.getStyle().set("margin", "5px");

        Anchor einsatzLink = new Anchor("einsaetze", "Einsatzplanung");
        einsatzLink.getStyle().set("font-size", "1.1em");
        einsatzLink.getStyle().set("margin", "5px");

        // Alle Links dem Container hinzufügen
        linksLayout.add(dashboardLink, ressortLink, helferLink, schichtLink, einsatzLink);

        // Alle Komponenten der Hauptansicht hinzufügen
        add(title, subtitle, modulesTitle, linksLayout);
    }
}
