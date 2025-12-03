package ch.flossrennen.eventmanagementsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Hauptklasse der Event Management System Applikation.
 *
 * Diese Spring Boot Anwendung verwaltet Events, Ressorts, Helfer, Schichten und Einsätze.
 * Die Applikation verwendet:
 * - Spring Boot 3.x für die Backend-Infrastruktur
 * - Vaadin 24 für die Web-Oberfläche
 * - PostgreSQL als Datenbank
 * - Docker für Containerisierung
 *
 * Profile:
 * - dev (default): Entwicklung mit lokaler PostgreSQL-Datenbank auf Port 5432
 * - prod: Produktion mit optimierten Einstellungen
 *
 * Start der Anwendung:
 * - Entwicklung: ./mvnw spring-boot:run
 * - Produktion: docker compose up --build
 */
@SpringBootApplication
public class EventManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventManagementSystemApplication.class, args);
    }

}
