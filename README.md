# Event Management System - Flossrennen

Ein vollständiges Spring Boot-basiertes Event Management System für die Verwaltung von Helfern, Einsätzen, Schichten und Ressorts beim Flossrennen-Event.

## Funktionsübersicht

### Implementierte Anforderungen

#### Muss-Anforderungen (MFA)
- ✅ **MFA.01 - Helferverwaltung**: Vollständige CRUD-Operationen für Helfer mit Stammressort-Zuordnung
- ✅ **MFA.02 - Einsatzplanung**: Erstellen und Verwalten von Einsätzen mit Zeitfenstern, Ressorts und Helfer-Zuordnung
- ✅ **MFA.03 - Validierung**: Automatische Prüfung gegen Doppelzuweisungen von Helfern im gleichen Zeitraum
- ✅ **MFA.04 - Rollen und Rechtekonzept**: Benutzer-Entity mit Rollen (Administrator, Ressortleiter) und BCrypt-Verschlüsselung
- ✅ **MFA.05 - Datenbankintegration**: PostgreSQL mit JPA/Hibernate, 3. Normalform
- ⏳ **MFA.06 - Dokumentation**: In Arbeit
- ✅ **MFA.07 - Schichtverwaltung**: Organisation von Einsätzen in Schichten mit Zeitfenstern
- ✅ **MFA.08 - Ressortmanagement**: Verwaltung von Ressorts mit Attributen
- ✅ **MFA.09 - Suchfunktion**: SearchService mit Filterung nach Ressort, Datum, Helfer, Status

#### Kann-Anforderungen (KFA)
- ⏳ **KFA.01 - Auswertungen**: In Planung (PDF/Excel Export)
- ✅ **KFA.02 - Import/Export**: CSV-Import und -Export für Helferdaten implementiert
- ✅ **KFA.03 - Dashboard**: Übersichtsseite mit Kennzahlen pro Ressort und Status
- ⏳ **KFA.04 - Änderungshistorie**: Nicht implementiert

#### Optionale Anforderungen (OFA)
- ⏳ **OFA.01 - Kommunikationsfunktionen**: Nicht implementiert
- ⏳ **OFA.02 - Qualifikationsverwaltung**: Nicht implementiert
- ⏳ **OFA.03 - Kalendersynchronisation**: Nicht implementiert
- ⏳ **OFA.04 - Mobile Ansicht**: Vaadin ist responsive-ready
- ✅ **OFA.05 - Containerbasiertes Deployment**: Docker Compose Setup vorhanden

## Technologien

- **Java 21** - Programmiersprache
- **Spring Boot 3.5.7** - Backend-Framework
- **Vaadin 24.9.4** - UI-Framework (serverseitig)
- **PostgreSQL 17** - Relationale Datenbank
- **H2** - In-Memory Datenbank für Tests
- **Docker & Docker Compose** - Containerisierung
- **JUnit 5 & Mockito** - Testing Framework
- **JaCoCo** - Test Coverage
- **Lombok** - Reduzierung von Boilerplate-Code

## Architektur

Das System folgt einer klassischen 3-Schichten-Architektur:

### Schichten
1. **Presentation Layer** (Views): Vaadin-basierte Web-UI
2. **Business Logic Layer** (Services): Geschäftslogik und Validierung
3. **Data Access Layer** (Repositories): JPA/Hibernate Datenzugriff

### Datenmodell (3NF)

**Entitäten:**
- `Helfer` - Helfer mit Kontaktdaten und Stammressort
- `Ressort` - Organisationseinheiten (z.B. Küche, Bar, Sicherheit)
- `Schicht` - Zeitfenster für Einsätze
- `Einsatz` - Konkrete Arbeitseinsätze mit Zeit, Ort und zugewiesenen Helfern
- `Benutzer` - Systembenutzer mit Rollen

**Beziehungen:**
- Helfer ↔ Ressort (n:1) - Stammressort-Zuordnung
- Helfer ↔ Einsatz (n:m) - Helfer-Zuweisungen
- Einsatz ↔ Ressort (n:1) - Ressort-Zuordnung
- Einsatz ↔ Schicht (n:1) - Schicht-Zuordnung
- Benutzer ↔ Ressort (n:1) - Ressortleiter-Zuordnung

## Installation und Ausführung

### Voraussetzungen
- Docker & Docker Compose (empfohlen)
- ODER: Java 21 + Maven + PostgreSQL

### Option 1: Mit Docker (Empfohlen)

```bash
# Repository klonen
git clone <repository-url>
cd event-management-system-tech-prototyp

# Anwendung bauen und starten
chmod +x build-and-run.sh
./build-and-run.sh

# Alternativ manuell:
./mvnw clean package -DskipTests
docker compose up --build -d
```

Die Anwendung ist dann unter http://localhost:8080 erreichbar.

### Option 2: Lokale Ausführung

```bash
# PostgreSQL starten und Datenbank erstellen
createdb eventmanagement

# Anwendung starten
./mvnw spring-boot:run
```

## Verwendung

### Module

1. **Dashboard** (`/dashboard`)
   - Übersicht über alle Kennzahlen
   - Anzahl Einsätze, Helfer, Ressorts, Schichten
   - Status-Übersicht (Offen, In Planung, Vollständig)
   - Helfer-Statistiken pro Ressort

2. **Ressortverwaltung** (`/ressorts`)
   - Anlegen, Bearbeiten, Löschen von Ressorts
   - Attribute: Name, Beschreibung, Zuständigkeiten, Kontaktperson

3. **Helferverwaltung** (`/helfer`)
   - CRUD-Operationen für Helfer
   - CSV-Import und -Export (via CsvService)
   - Stammressort-Zuordnung

4. **Schichtverwaltung** (`/schichten`)
   - Anlegen von Schichten mit Zeitfenstern
   - Gruppierung von Einsätzen

5. **Einsatzplanung** (`/einsaetze`)
   - Erstellen von Einsätzen
   - Helfer-Zuweisung mit automatischer Konfliktprüfung
   - Status-Tracking (Offen, In Planung, Vollständig, Abgeschlossen)

## Testing

### Tests ausführen
```bash
./mvnw test
```

### Test-Coverage Report
```bash
./mvnw clean test jacoco:report
# Report ansehen: target/site/jacoco/index.html
```

### Aktuelle Test-Abdeckung
- **36 Unit Tests** (alle bestanden)
- **Service Layer**: 49% Coverage
- **Model Layer**: 100% Coverage
- **Gesamt**: 11% (Views nicht getestet)

### Test-Kategorien
- **Model Tests**: Entitäts-Tests (HelferTest)
- **Service Tests**: Business-Logik Tests
  - EinsatzServiceTest: Validierung, Konfliktprüfung (7 Tests)
  - CsvServiceTest: Import/Export (4 Tests)
  - RessortServiceTest: CRUD-Operationen (5 Tests)
  - SchichtServiceTest: Zeitvalidierung (2 Tests)
- **Repository Tests**: Datenzugriff-Tests (HelferRepositoryTest)

## Konfiguration

### Umgebungsvariablen

```bash
# Datenbank
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/eventmanagement
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres

# JPA
SPRING_JPA_HIBERNATE_DDL_AUTO=update
```

### application.properties

Siehe `src/main/resources/application.properties` für alle Konfigurationsoptionen.

## Docker

### Images
- `Dockerfile.app` - Multi-Stage Build für Production (verwendet von docker-compose)
- `Dockerfile` - GraalVM Native Image (optional, für optimale Performance)
- `Dockerfile.dev` - Development Image mit Debug-Support

### Docker Compose
```yaml
services:
  postgresdb:  # PostgreSQL Datenbank
    image: postgres:17-alpine
    ports: ["5432:5432"]
    
  app:         # Spring Boot Anwendung
    build: .
    ports: ["8080:8080"]
    depends_on: [postgresdb]
```

## API / Services

### Key Services

**EinsatzService**
- `assignHelfer(einsatzId, helfer)` - Helfer zuweisen mit Konfliktprüfung
- `isHelferAvailable(helfer, start, end)` - Verfügbarkeit prüfen
- `findOverlappingEinsaetze(...)` - Überschneidungen finden

**CsvService**
- `exportHelferToCsv()` - Helfer als CSV exportieren
- `importHelferFromCsv(csvContent)` - Helfer aus CSV importieren

**DashboardService**
- `getDashboardData()` - Alle Kennzahlen abrufen

**SearchService**
- `searchEinsaetze(...)` - Einsätze nach Kriterien suchen

## Sicherheit

- **Passwort-Verschlüsselung**: BCrypt (via Spring Security)
- **Benutzer-Rollen**: Administrator, Ressortleiter
- **Authentifizierung**: Grundstruktur vorhanden, UI-Integration ausstehend

## Performance

- Zielwert: < 2 Sekunden Antwortzeit (NFA.02)
- Datenbankabfragen optimiert mit JPA Fetch Strategies
- Lazy Loading für große Collections

## Bekannte Einschränkungen

1. **Authentifizierung**: Noch nicht in UI integriert (permitAll)
2. **Rollenbasierte Zugriffskontrolle**: Service-Struktur vorhanden, aber nicht aktiviert
3. **PDF/Excel Export**: Noch nicht implementiert
4. **Email-Benachrichtigungen**: Nicht implementiert
5. **Qualifikationsverwaltung**: Nicht implementiert

## Entwicklung

### Projekt bauen
```bash
./mvnw clean package
```

### Development Mode
```bash
./mvnw spring-boot:run
```

### Tests schreiben
- Unit Tests mit Mockito in `src/test/java/.../service/`
- Integration Tests mit `@DataJpaTest` in `src/test/java/.../repository/`
- Testdatenbank: H2 in-memory

## Dokumentation

- Siehe [DOCKER.md](DOCKER.md) für Docker-spezifische Anweisungen
- JavaDoc in Quellcode
- Architektur-Diagramme: (TBD)

## Lizenz

[Add your license information here]

## Autoren

Entwickelt als technischer Prototyp für das Event Management System des Flossrennens.
