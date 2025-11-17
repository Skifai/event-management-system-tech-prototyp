package ch.flossrennen.eventmanagementsystem.views;

import ch.flossrennen.eventmanagementsystem.service.DashboardService;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

/**
 * Dashboard-Ansicht mit Übersicht über wichtige Kennzahlen
 * Implementiert KFA.03 - Dashboard
 */
@Route("dashboard")
public class DashboardView extends VerticalLayout {

    private final DashboardService dashboardService;

    public DashboardView(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
        
        setSizeFull();
        setPadding(true);
        setSpacing(true);
        
        H1 title = new H1("Dashboard");
        title.addClassNames(LumoUtility.TextColor.PRIMARY);
        
        add(title);
        
        loadDashboardData();
    }
    
    private void loadDashboardData() {
        DashboardService.DashboardData data = dashboardService.getDashboardData();
        
        // Gesamtübersicht
        H2 overviewTitle = new H2("Gesamtübersicht");
        HorizontalLayout overviewLayout = new HorizontalLayout();
        overviewLayout.setWidthFull();
        overviewLayout.setSpacing(true);
        
        overviewLayout.add(
            createStatCard("Einsätze", String.valueOf(data.getGesamtEinsaetze()), "blue"),
            createStatCard("Helfer", String.valueOf(data.getGesamtHelfer()), "green"),
            createStatCard("Ressorts", String.valueOf(data.getGesamtRessorts()), "orange"),
            createStatCard("Schichten", String.valueOf(data.getGesamtSchichten()), "purple")
        );
        
        add(overviewTitle, overviewLayout);
        
        // Einsatzstatus
        H2 statusTitle = new H2("Einsatzstatus");
        HorizontalLayout statusLayout = new HorizontalLayout();
        statusLayout.setWidthFull();
        statusLayout.setSpacing(true);
        
        statusLayout.add(
            createStatCard("Offen", String.valueOf(data.getOffeneEinsaetze()), "red"),
            createStatCard("In Planung", String.valueOf(data.getInPlanungEinsaetze()), "yellow"),
            createStatCard("Vollständig", String.valueOf(data.getVollstaendigeEinsaetze()), "green")
        );
        
        add(statusTitle, statusLayout);
        
        // Ressort-Statistiken
        H2 ressortTitle = new H2("Statistik pro Ressort");
        VerticalLayout ressortLayout = new VerticalLayout();
        ressortLayout.setWidthFull();
        ressortLayout.setSpacing(false);
        
        data.getRessortStatistiken().values().forEach(stats -> {
            Div ressortCard = createRessortCard(stats);
            ressortLayout.add(ressortCard);
        });
        
        add(ressortTitle, ressortLayout);
    }
    
    private Div createStatCard(String label, String value, String color) {
        Div card = new Div();
        card.getStyle()
            .set("border", "2px solid var(--lumo-contrast-10pct)")
            .set("border-radius", "8px")
            .set("padding", "20px")
            .set("text-align", "center")
            .set("background-color", getColorForCard(color))
            .set("flex", "1");
        
        Paragraph labelPara = new Paragraph(label);
        labelPara.getStyle()
            .set("margin", "0")
            .set("font-size", "0.9em")
            .set("color", "var(--lumo-secondary-text-color)");
        
        Paragraph valuePara = new Paragraph(value);
        valuePara.getStyle()
            .set("margin", "10px 0 0 0")
            .set("font-size", "2em")
            .set("font-weight", "bold");
        
        card.add(labelPara, valuePara);
        return card;
    }
    
    private Div createRessortCard(DashboardService.RessortStats stats) {
        Div card = new Div();
        card.getStyle()
            .set("border", "1px solid var(--lumo-contrast-10pct)")
            .set("border-radius", "4px")
            .set("padding", "15px")
            .set("margin-bottom", "10px")
            .set("background-color", "var(--lumo-base-color)");
        
        H2 name = new H2(stats.getRessortName());
        name.getStyle().set("margin", "0 0 10px 0").set("font-size", "1.2em");
        
        HorizontalLayout statsLayout = new HorizontalLayout();
        statsLayout.setWidthFull();
        statsLayout.setSpacing(true);
        
        statsLayout.add(
            createMiniStatCard("Einsätze", String.valueOf(stats.getAnzahlEinsaetze())),
            createMiniStatCard("Benötigt", String.valueOf(stats.getBenoetigteHelfer())),
            createMiniStatCard("Zugewiesen", String.valueOf(stats.getZugewieseneHelfer())),
            createMiniStatCard("Fehlend", String.valueOf(stats.getFehlendeHelfer()), 
                              stats.getFehlendeHelfer() > 0 ? "red" : "green")
        );
        
        card.add(name, statsLayout);
        return card;
    }
    
    private Div createMiniStatCard(String label, String value) {
        return createMiniStatCard(label, value, null);
    }
    
    private Div createMiniStatCard(String label, String value, String colorHint) {
        Div card = new Div();
        card.getStyle()
            .set("text-align", "center")
            .set("flex", "1");
        
        Paragraph labelPara = new Paragraph(label);
        labelPara.getStyle()
            .set("margin", "0")
            .set("font-size", "0.8em")
            .set("color", "var(--lumo-secondary-text-color)");
        
        Paragraph valuePara = new Paragraph(value);
        valuePara.getStyle()
            .set("margin", "5px 0 0 0")
            .set("font-size", "1.5em")
            .set("font-weight", "bold");
        
        if (colorHint != null) {
            valuePara.getStyle().set("color", getColorValue(colorHint));
        }
        
        card.add(labelPara, valuePara);
        return card;
    }
    
    private String getColorForCard(String color) {
        return switch (color) {
            case "blue" -> "var(--lumo-primary-color-10pct)";
            case "green" -> "var(--lumo-success-color-10pct)";
            case "orange" -> "var(--lumo-warning-color-10pct)";
            case "purple" -> "var(--lumo-contrast-10pct)";
            case "red" -> "var(--lumo-error-color-10pct)";
            case "yellow" -> "var(--lumo-warning-color-10pct)";
            default -> "var(--lumo-contrast-5pct)";
        };
    }
    
    private String getColorValue(String color) {
        return switch (color) {
            case "red" -> "var(--lumo-error-color)";
            case "green" -> "var(--lumo-success-color)";
            case "orange", "yellow" -> "var(--lumo-warning-color)";
            default -> "var(--lumo-primary-text-color)";
        };
    }
}
