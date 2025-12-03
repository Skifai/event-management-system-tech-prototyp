package ch.flossrennen.eventmanagementsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Hauptklasse der Event Management System Applikation.
 * <p>
 * Diese Spring Boot Anwendung verwaltet Events, Ressorts, Helfer, Schichten und Einsätze.
 * Die Applikation verwendet:
 * <ul>
 * <li>Spring Boot 3.x für die Backend-Infrastruktur</li>
 * <li>Vaadin 24 für die Web-Oberfläche</li>
 * <li>PostgreSQL als Datenbank</li>
 * <li>Docker für Containerisierung</li>
 * </ul>
 * <p>
 * Profile:
 * <ul>
 * <li>dev (default): Entwicklung mit lokaler PostgreSQL-Datenbank auf Port 5432</li>
 * <li>prod: Produktion mit optimierten Einstellungen</li>
 * </ul>
 * <p>
 * Start der Anwendung:
 * <ul>
 * <li>Entwicklung: ./mvnw spring-boot:run</li>
 * <li>Produktion: docker compose up --build</li>
 * </ul>
 */
@SpringBootApplication
public class EventManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventManagementSystemApplication.class, args);
    }

}
