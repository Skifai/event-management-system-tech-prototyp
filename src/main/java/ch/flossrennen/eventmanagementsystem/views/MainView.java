package ch.flossrennen.eventmanagementsystem.views;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

@Route("")
public class MainView extends VerticalLayout {

    public MainView() {
        setSpacing(true);
        setPadding(true);
        setAlignItems(Alignment.CENTER);

        H1 title = new H1("Eventmanagementsystem Flossrennen");
        title.addClassNames(LumoUtility.TextColor.PRIMARY);

        Paragraph subtitle = new Paragraph("Von Handarbeit zu Klickarbeit - smarte Planung für das Flossrennen");

        H2 modulesTitle = new H2("Module");
        
        VerticalLayout linksLayout = new VerticalLayout();
        linksLayout.setSpacing(false);
        linksLayout.setPadding(false);
        linksLayout.setAlignItems(Alignment.CENTER);
        
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
        
        linksLayout.add(ressortLink, helferLink, schichtLink, einsatzLink);

        add(title, subtitle, modulesTitle, linksLayout);
    }
}
