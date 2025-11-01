package ch.flossrennen.eventmanagementsystem.views;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
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

        Anchor helferLink = new Anchor("helfer", "Helferverwaltung");
        helferLink.getStyle().set("font-size", "1.2em");
        helferLink.getStyle().set("margin-top", "20px");

        //Some Comment
        add(title, subtitle, helferLink);
    }
}
