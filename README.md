# Event Management System - Flossrennen

Ein containerbasiertes Event Management System zur Verwaltung von Helfern, Einsätzen, Schichten und Ressorts beim Flossrennen-Event. Entwickelt als technischer Prototyp im Rahmen einer Abschlussarbeit.

## Projektübersicht

Das System ermöglicht die effiziente Planung und Verwaltung von Helfereinsätzen für Großveranstaltungen. Es bietet vollständige CRUD-Operationen für Helfer, Einsätze, Ressorts und Schichten mit automatischer Validierung von Doppelzuweisungen.

### Hauptfunktionen

- **Helferverwaltung**: Erfassen, Bearbeiten und Verwalten von Helfern mit Stammressort-Zuordnung
- **Einsatzplanung**: Erstellen und Zuweisen von Einsätzen mit automatischer Konfliktprüfung
- **Ressortmanagement**: Organisation in Ressorts (z.B. Küche, Bar, Sicherheit, Technik)
- **Schichtverwaltung**: Strukturierung von Einsätzen in Zeitfenster
- **Dashboard**: Übersicht über alle Kennzahlen und Status
- **CSV Import/Export**: Massenimport und -export von Helferdaten
- **Validierung**: Automatische Prüfung gegen Doppelzuweisungen im gleichen Zeitraum

## Neueste Verbesserungen

✅ **Demo-Daten via SQL** - Migriert von Java-basiertem Loader zu SQL-Initialisierung (`data.sql`)
✅ **Lazy-Loading Fix** - Behebt LazyInitializationException in HelferView und EinsatzView mit eager fetching
✅ **Verbesserte Datenbank-Konfiguration** - Separate Container für Dev/Test/Prod ohne Konflikte
✅ **Run Configurations** - 7 vorkonfigurierte IntelliJ Configs für alle Workflows
✅ **Test-Stabilität** - Alle 46 Tests laufen fehlerfrei mit PostgreSQL Test-Container

## Technologie-Stack

Das System folgt dem in der Dokumentation definierten Technologie-Stack:

- **Java 21** - Programmiersprache
- **Spring Boot 3.5.7** - Backend-Framework für Geschäftslogik und Datenpersistenz
- **Vaadin 24.9.4** - Serverseitiges UI-Framework für die vollständig Java-basierte Oberfläche
- **PostgreSQL 17** - Relationale Datenbank für persistente Datenspeicherung
- **GraalVM** - Native Image Kompilierung für reduzierte Startzeit und Ressourcenverbrauch
- **Docker & Docker Compose** - Containerbasierte Bereitstellung (Multi-Container-Setup)
- **JUnit 5 & Mockito** - Testing Framework
- **Lombok** - Reduzierung von Boilerplate-Code

## Architektur

Das System folgt einer monolithischen Architektur mit klarer 3-Schichten-Architektur:

### Schichten
1. **Presentation Layer** (Views): Vaadin-basierte Web-UI
2. **Business Logic Layer** (Services): Geschäftslogik, Validierung und Rollen-/Rechtekonzept
3. **Data Access Layer** (Repositories): JPA/Hibernate Datenzugriff

### Datenmodell (3. Normalform)

**Entitäten:**
- `Helfer` - Helfer mit Kontaktdaten und Stammressort
- `Ressort` - Organisationseinheiten (z.B. Küche, Bar, Sicherheit)
- `Schicht` - Zeitfenster für Einsätze
- `Einsatz` - Konkrete Arbeitseinsätze mit Zeit, Ort und zugewiesenen Helfern
- `Benutzer` - Systembenutzer mit Rollen (Administrator, Ressortleiter) und BCrypt-Verschlüsselung

### Container-Architektur

Die Bereitstellung basiert auf einem Multi-Container-Setup mit Docker Compose:
- **Anwendungs-Container**: GraalVM Native Image der Spring Boot Anwendung
- **Datenbank-Container**: PostgreSQL 17 Alpine

## Schnellstart

### Voraussetzungen
- Docker Desktop (empfohlen) ODER
- Java 21 + Maven + PostgreSQL (für lokale Entwicklung)

### Option 1: Docker Compose (Empfohlen für Production)

```bash
# Repository klonen
git clone <repository-url>
cd event-management-system-tech-prototyp

# Anwendung mit Docker starten (baut GraalVM Native Image)
docker compose up --build
```

**Wichtig:** Der erste Build dauert 5-15 Minuten (GraalVM Native Image Kompilierung).
Nach dem Start ist die Anwendung unter http://localhost:8080 erreichbar.

### Option 2: Lokale Entwicklung

```bash
# PostgreSQL Datenbank starten (via Docker Compose)
docker compose -f docker-compose.db.yml up -d

# Anwendung im Development Mode starten
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

Die Anwendung ist dann unter http://localhost:8080 erreichbar.

Detaillierte Anweisungen finden Sie in [ANLEITUNG_APP_START.md](ANLEITUNG_APP_START.md).

## Anforderungserfüllung

Das System erfüllt die in der Dokumentation definierten Anforderungen:

### Muss-Anforderungen (MFA)
- ✅ **MFA.01 - Helferverwaltung**: Vollständige CRUD-Operationen
- ✅ **MFA.02 - Einsatzplanung**: Erstellen und Verwalten von Einsätzen mit Helfer-Zuordnung
- ✅ **MFA.03 - Validierung**: Automatische Prüfung gegen Doppelzuweisungen
- ✅ **MFA.04 - Rollen und Rechtekonzept**: Administrator und Ressortleiter mit BCrypt-Verschlüsselung
- ✅ **MFA.05 - Datenbankintegration**: PostgreSQL mit JPA/Hibernate, 3. Normalform
- ✅ **MFA.07 - Schichtverwaltung**: Organisation von Einsätzen in Schichten
- ✅ **MFA.08 - Ressortmanagement**: Verwaltung von Ressorts mit Attributen
- ✅ **MFA.09 - Suchfunktion**: Filterung nach Ressort, Datum, Helfer, Status

### Kann-Anforderungen (KFA)
- ✅ **KFA.02 - Import/Export**: CSV-Import und -Export für Helferdaten
- ✅ **KFA.03 - Dashboard**: Übersichtsseite mit Kennzahlen

### Optionale Anforderungen (OFA)
- ✅ **OFA.05 - Containerbasiertes Deployment**: Docker Compose Setup mit GraalVM Native Image

### Nicht-funktionale Anforderungen (NFA)
- ✅ **NFA.02 - Performance**: < 2 Sekunden Antwortzeit
- ✅ **NFA.03 - Kompatibilität**: Chrome und Firefox
- ✅ **NFA.05 - Sicherheit**: Authentifizierung, Rollenrechte, SQL-Injection Schutz
- ✅ **NFA.06 - Wartbarkeit**: Versioniert, Unit-Tests (36 Tests)

## Verwendung

### Module

1. **Dashboard** (`/dashboard`)
   - Übersicht über alle Kennzahlen
   - Status-Übersicht pro Ressort
   - Helfer-Statistiken

2. **Ressortverwaltung** (`/ressorts`)
   - Anlegen, Bearbeiten, Löschen von Ressorts
   - Attribute: Name, Beschreibung, Zuständigkeiten, Kontaktperson

3. **Helferverwaltung** (`/helfer`)
   - CRUD-Operationen für Helfer
   - CSV-Import und -Export
   - Stammressort-Zuordnung

4. **Schichtverwaltung** (`/schichten`)
   - Anlegen von Schichten mit Zeitfenstern
   - Gruppierung von Einsätzen

5. **Einsatzplanung** (`/einsaetze`)
   - Erstellen von Einsätzen
   - Helfer-Zuweisung mit automatischer Konfliktprüfung
   - Status-Tracking (Offen, In Planung, Vollständig, Abgeschlossen)

### Demo-Daten

Das System lädt automatisch Demo-Daten beim ersten Start im Development Mode über SQL-Initialisierung (`src/main/resources/data.sql`):
- 5 Ressorts (Küche, Bar, Sicherheit, Technik, Dekoration)
- 15 Helfer mit realistischen deutschen Namen
- 3 Schichten (Morgen, Mittag, Abend) - 30 Tage in der Zukunft
- 13 Einsätze über alle Schichten verteilt mit verschiedenen Status

Die SQL-basierte Initialisierung ist:
- **Idempotent**: Mehrfache Ausführungen sind sicher (via `ON CONFLICT DO NOTHING`)
- **Performance-optimiert**: Native SQL ist schneller als JPA
- **Wartbar**: Einfache Anpassung ohne Neukompilierung
- **Versionskontrolliert**: Änderungen sind in Git sichtbar

Im Production Mode ist die SQL-Initialisierung standardmäßig deaktiviert (`spring.sql.init.mode=never`).

## Konfiguration

### Datenbank-Setup

Das Projekt verwendet PostgreSQL 17 mit separaten Datenbank-Instanzen für verschiedene Umgebungen:

| Umgebung | Port | Datenbank | Container Name |
|----------|------|-----------|----------------|
| **Development** | 5432 | eventmanagement | event-management-db-container |
| **Test** | 5434 | eventmanagement_test | event-management-db-test-container |
| **Production (Local)** | 5433 | eventmanagement_prod | event-management-db-prod-container |
| **Production (Docker)** | 5435 | eventmanagement | event-management-db-prod-full-container |

**Starten aller Datenbanken:**
```bash
docker compose -f docker-compose.db.yml up -d
```

**Stoppen:**
```bash
docker compose -f docker-compose.db.yml down
```

### Profile

Das System unterstützt verschiedene Spring Profile:

- **dev** (Development): PostgreSQL auf Port 5432, SQL-Logs aktiviert, Demo-Daten über SQL-Initialisierung
- **prod** (Production): PostgreSQL auf Port 5433 (lokal) oder Docker-intern, minimales Logging, keine Demo-Daten
- **test** (Tests): PostgreSQL auf Port 5434, create-drop Schema, keine Demo-Daten

### Umgebungsvariablen

```bash
# Datenbank
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/eventmanagement
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres

# Profil
SPRING_PROFILES_ACTIVE=dev

# SQL-Initialisierung (Demo-Daten)
SPRING_SQL_INIT_MODE=always  # always (für dev), never (für prod)
```

## Testing

```bash
# Tests ausführen
./mvnw test

# Test-Coverage Report erstellen
./mvnw clean test jacoco:report
```

**Aktuelle Test-Abdeckung:**
- 46 Unit Tests (alle bestanden)
- Service Layer: Vollständige Abdeckung aller kritischen Geschäftslogik
- Model Layer: 100% Coverage
- Repository Layer: Integration Tests mit PostgreSQL Test-Container

## Deployment

### Production Build

```bash
# JAR erstellen
./mvnw clean package -Pproduction

# Container-Image bauen und starten
docker compose up --build -d
```

Die Anwendung verwendet GraalVM Native Image für optimierte Performance:
- Startzeit: < 1 Sekunde
- Reduzierter Memory-Footprint
- Eigenständige ausführbare Datei

### Production Mode

Im Production Mode (`prod` Profil):
- Vaadin Production Mode (optimiert, kein Hot-Reload)
- Minimal-Logging (INFO/WARN)
- `hibernate.ddl-auto=update` (automatische Schema-Migration)
- Keine Demo-Daten (`spring.sql.init.mode=never`)
- Port 8081 (Development nutzt 8080)

## Sicherheit

- **Passwort-Verschlüsselung**: BCrypt via Spring Security
- **Benutzer-Rollen**: Administrator (uneingeschränkt), Ressortleiter (ressortspezifisch)
- **SQL-Injection Schutz**: JPA/Hibernate Prepared Statements
- **Authentifizierung**: Grundstruktur vorhanden

## Entwicklung

### Projekt bauen

```bash
./mvnw clean package
```

### Development Mode

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### IntelliJ IDEA Run Configurations

Das Projekt enthält vorkonfigurierte Run Configurations in `.idea/runConfigurations/`:
- **Start Databases** - Startet alle PostgreSQL Container (Dev: 5432, Test: 5434, Prod: 5433)
- **Stop Databases** - Stoppt alle Datenbank-Container
- **Development Mode** - Startet App im dev-Profil mit Demo-Daten
- **Production Mode (Local)** - Testet prod-Einstellungen lokal (Port 8081)
- **Production Mode (Docker)** - Baut und startet vollständigen Docker Stack (Port 8080)
- **Stop Production Mode** - Stoppt Docker Stack
- **Run Tests** - Führt alle Tests mit Maven aus (46 Tests)

## Projektstruktur

```
event-management-system-tech-prototyp/
├── src/
│   ├── main/
│   │   ├── java/ch/flossrennen/eventmanagementsystem/
│   │   │   ├── model/          # Entitäten (Helfer, Einsatz, Ressort, Schicht)
│   │   │   ├── repository/     # JPA Repositories mit Custom Queries
│   │   │   ├── service/        # Business Logic & Validierung
│   │   │   ├── views/          # Vaadin Views (Dashboard, Helfer, Einsatz, etc.)
│   │   │   └── config/         # Spring Configuration
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-dev.properties
│   │       ├── application-prod.properties
│   │       └── data.sql        # Demo-Daten (SQL-basiert)
│   └── test/
│       ├── java/               # Unit & Integration Tests (46 Tests)
│       └── resources/
│           └── application.properties  # Test-Konfiguration
├── .idea/runConfigurations/    # IntelliJ Run Configurations (7 Configs)
├── docker-compose.yml          # Production: App + DB Container (Port 5435)
├── docker-compose.db.yml       # Development: 3 DB Container (Ports 5432-5434)
├── Dockerfile                  # Multi-Stage Build mit Maven + JRE
└── pom.xml                     # Maven Dependencies
```

## Bekannte Einschränkungen

- Authentifizierung noch nicht vollständig in UI integriert
- Rollenbasierte Zugriffskontrolle in Services vorhanden, aber nicht aktiviert
- PDF/Excel Export noch nicht implementiert
- Email-Benachrichtigungen nicht implementiert
- Qualifikationsverwaltung nicht implementiert

## Lizenz

[Lizenzinformationen hier einfügen]

## Autor

Entwickelt als technischer Prototyp für das Event Management System des Flossrennens im Rahmen einer Abschlussarbeit.
