# Event Management System - Flossrennen

Ein vollständiges Spring Boot-basiertes Event Management System für die Verwaltung von Helfern, Einsätzen, Schichten und Ressorts beim Flossrennen-Event.

[![CI - Build and Test](https://github.com/Skifai/event-management-system-tech-prototyp/actions/workflows/ci.yml/badge.svg)](https://github.com/Skifai/event-management-system-tech-prototyp/actions/workflows/ci.yml)
[![Package - Create JAR Artifact](https://github.com/Skifai/event-management-system-tech-prototyp/actions/workflows/package.yml/badge.svg)](https://github.com/Skifai/event-management-system-tech-prototyp/actions/workflows/package.yml)

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

### Option 1: IntelliJ IDEA (Empfohlen für Entwicklung)

Das Projekt enthält vorkonfigurierte IntelliJ IDEA Run Configurations für einfache Entwicklung und Produktion.

**Neue, verbesserte Run Configurations (November 2024)**:
- ✅ **Unabhängige Container-Verwaltung** - Datenbanken werden separat verwaltet
- ✅ Native Spring Boot Application Konfigurationen (statt Maven)
- ✅ Profile über IDEA's `Active profiles` Feld
- ✅ Bessere Integration mit IDEA's Spring Boot Tools
- ✅ Schnellerer Start, besseres Debugging

#### Starten in IDEA - Schritt für Schritt

**Voraussetzungen:**
- IntelliJ IDEA installiert (Community oder Ultimate)
- Docker Desktop läuft
- Java 21 konfiguriert in IDEA

**Schritt 1: Projekt öffnen**
1. IntelliJ IDEA starten
2. `File` → `Open...`
3. Projektverzeichnis auswählen
4. Warten bis Maven-Import abgeschlossen ist

**Schritt 2: Datenbank-Container starten** ⚠️ **Wichtig: Einmalig erforderlich**
1. Run Configuration **"Start Databases"** aus Dropdown auswählen
2. ▶️ Run-Button klicken
3. Container laufen nun im Hintergrund und müssen nicht mehr neu gestartet werden

**Schritt 3: Anwendung starten**

Die Run Configurations sind im Dropdown oben rechts in IDEA verfügbar:

---

#### 🔵 Development Mode (dev-Profil)

**Verwendung:**
1. Run Configuration **"Development Mode"** aus Dropdown auswählen
2. ▶️ Run-Button klicken **ODER** `Shift+F10` drücken
3. Warten bis "Started EventManagementSystemApplication" im Log erscheint
4. Browser öffnet automatisch: http://localhost:8080

**Was passiert:**
- Spring Boot startet mit **`dev` Profil** (verwendet `application-dev.properties`)
- DatabaseStartupListener prüft, ob PostgreSQL Container läuft
- Verbindet sich mit existierendem Container:
  - Container-Name: `event-management-db-container`
  - Port: `5432`
  - Database: `eventmanagement`
- Vaadin läuft im Development-Mode (Hot-Reload aktiv)
- SQL-Logs werden in Console angezeigt
- Testdaten werden automatisch geladen

**Datenbank-Details:**
```properties
Host: localhost
Port: 5432
Database: eventmanagement
Username: postgres
Password: postgres
```

**Konfigurationstyp**: Spring Boot Application (nicht Maven)  
**Profile**: `dev`

**Hinweis:** Falls Docker nicht läuft, startet die App trotzdem, erwartet aber eine externe PostgreSQL auf Port 5432.

---

#### 🟢 Production Mode (Local) - prod-Profil

**NEU**: Schnelles Testen von Production-Einstellungen ohne Docker!

**Verwendung:**
1. **Voraussetzung**: Datenbank-Container müssen laufen (siehe "Start Databases")
2. Run Configuration **"Production Mode (Local)"** auswählen
3. ▶️ Run-Button klicken
4. App verfügbar: http://localhost:8081

**Was passiert:**
- Spring Boot startet mit **`prod` Profil** (verwendet `application-prod.properties`)
- Verwendet Production-Einstellungen (optimiert, minimal logging)
- Läuft auf Port **8081** (unterschiedlich von Development)
- Verbindet sich mit Production-DB auf Port **5433**
- DatabaseStartupListener prüft Container-Verfügbarkeit
- **Keine** Testdaten

**Datenbank-Details:**
```properties
Host: localhost
Port: 5433  # NICHT 5432!
Database: eventmanagement_prod
Username: postgres
Password: postgres
```

**Konfigurationstyp**: Spring Boot Application  
**Profile**: `prod`

**Wann verwenden**: Schnelles Testen von Production-Settings ohne lange Docker-Build-Zeit

---

#### 🐳 Production Mode (Docker) - prod-Profil mit GraalVM

**Verwendung:**
1. Run Configuration **"Production Mode (Docker)"** aus Dropdown auswählen
2. ▶️ Run-Button klicken
3. **Warten** (erster Build dauert 5-15 Minuten für GraalVM Native Image)
4. Nach Abschluss: http://localhost:8081

**Was passiert:**
- Docker baut GraalVM Native Image (siehe `Dockerfile`)
- Startet **separate** Production-PostgreSQL auf Port `5433`
- Startet App-Container mit `SPRING_PROFILES_ACTIVE=prod`
- Verwendet `application-prod.properties`:
  - Database: `eventmanagement_prod` (Port 5433)
  - Vaadin Production-Mode (kein Hot-Reload)
  - Minimal-Logging (INFO statt DEBUG)
  - `hibernate.ddl-auto=validate` (statt update)

**Datenbank-Details:**
```properties
Host: localhost
Port: 5433  # NICHT 5432!
Database: eventmanagement_prod
Username: postgres
Password: postgres
```

**Konfigurationstyp**: Shell Script (Docker Compose)  
**Profile**: `prod`

**Wichtig:** 
- Production läuft auf Port **8081** (Development auf 8080)
- Beide Modi können parallel laufen (unterschiedliche Ports und Datenbanken)
- Erster Build dauert lange (GraalVM Compilation)

**Stoppen**: "Stop Production Mode" Run Configuration verwenden

---

#### ⚙️ Manueller Start mit eigenem Profil

Falls Sie ein eigenes Profil verwenden möchten:

**Via Run Configuration bearbeiten:**
1. Run Configuration Dropdown → `Edit Configurations...`
2. Neue **Spring Boot Application** Configuration erstellen
3. Main class: `ch.flossrennen.eventmanagementsystem.EventManagementSystemApplication`
4. Module: `event-management-system`
5. **Active profiles**: `ihr-profil` (z.B. `dev`, `prod`, `test`)
6. Environment variables (optional):
   ```
   SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/ihre_db
   SPRING_DATASOURCE_USERNAME=ihr_user
   SPRING_DATASOURCE_PASSWORD=ihr_passwort
   ```

**Via Maven:**
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

**Via JAR:**
```bash
java -jar -Dspring.profiles.active=prod target/event-management-system-0.0.1-SNAPSHOT.jar
```

---

#### 📋 Übersicht: Profile und Unterschiede

| Eigenschaft | Development (dev) | Production (Local) | Production (Docker) |
|-------------|-------------------|-------------------|---------------------|
| **Profile** | `dev` | `prod` | `prod` |
| **Run Config** | "Development Mode" | "Production Mode (Local)" | "Production Mode (Docker)" |
| **Config-Typ** | Spring Boot App | Spring Boot App | Shell Script |
| **App-Port** | 8080 | 8081 | 8081 |
| **DB-Port** | 5432 | 5433 | 5433 |
| **Database** | `eventmanagement` | `eventmanagement_prod` | `eventmanagement_prod` |
| **Container Management** | Benötigt "Start Databases" | Benötigt "Start Databases" | ✅ Eigene DB im Docker |
| **Hot-Reload** | ✅ Ja (DevTools) | ❌ Nein | ❌ Nein |
| **Vaadin Mode** | Development | Production | Production |
| **SQL Logs** | ✅ DEBUG | ⚠️ INFO/WARN | ⚠️ INFO/WARN |
| **DDL Auto** | `update` | `validate` | `validate` |
| **Build-Typ** | JAR | JAR | GraalVM Native Image |
| **Testdaten** | ✅ Ja | ❌ Nein | ❌ Nein |
| **Startup** | ~3-5 Sek | ~3-5 Sek | <1 Sek (Native) |
| **Build-Zeit** | Schnell | Schnell | 5-15 Min (erste) |

---

Detaillierte Dokumentation: [IDEA_SETUP.md](IDEA_SETUP.md) und [.idea/runConfigurations/README.md](.idea/runConfigurations/README.md)

### Option 2: Mit Docker (Empfohlen)

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

### Option 3: Lokale Ausführung

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

# Test Data (optional)
APP_TESTDATA_ENABLED=true
```

### Testdaten

Das System bietet die Möglichkeit, automatisch Testdaten beim Start zu laden. Dies ist besonders nützlich für Entwicklung und Demonstrations-Umgebungen.

#### Automatisches Laden in Development

In der **Development-Umgebung** (Standard-Profil) werden Testdaten **automatisch** beim ersten Start geladen:

```bash
# In application.properties ist bereits konfiguriert:
app.testdata.enabled=true
```

Die Testdaten umfassen:
- **5 Ressorts**: Küche, Bar, Sicherheit, Technik, Dekoration
- **15 Helfer**: Mit realistischen Namen, E-Mails und Telefonnummern
- **3 Schichten**: Morgen-, Mittags- und Abend-Schicht für einen Event-Tag
- **13 Einsätze**: Verschiedene Aufgaben über alle Schichten und Ressorts verteilt

**Wichtig**: Die Testdaten werden nur geladen, wenn die Datenbank noch leer ist. Bei bereits vorhandenen Daten wird der Ladevorgang übersprungen.

#### Testdaten in Production

In der **Production-Umgebung** (`prod`-Profil) sind Testdaten **standardmäßig deaktiviert**:

```properties
# In application-prod.properties:
app.testdata.enabled=false
```

Falls Sie dennoch Testdaten in Production laden möchten (z.B. für Demo-Zwecke), können Sie dies aktivieren:

**Option 1: Via application-prod.properties**
```properties
app.testdata.enabled=true
```

**Option 2: Via Umgebungsvariable**
```bash
export APP_TESTDATA_ENABLED=true
# oder beim Start:
java -jar -Dspring.profiles.active=prod -Dapp.testdata.enabled=true target/event-management-system-*.jar
```

**Option 3: Via Docker Compose**
```yaml
services:
  app:
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - APP_TESTDATA_ENABLED=true
```

#### Testdaten zurücksetzen

Um die Testdaten zurückzusetzen und neu zu laden:

1. **Development** (mit Docker):
   ```bash
   docker compose down -v  # Löscht auch Volumes
   docker compose up --build
   ```

2. **Manuell** (PostgreSQL):
   ```bash
   dropdb eventmanagement
   createdb eventmanagement
   # Anwendung neu starten
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

## CI/CD Pipeline

Das Projekt verwendet GitHub Actions für Continuous Integration und Continuous Delivery. Die Pipeline ist in mehrere fokussierte Workflows aufgeteilt:

**Pipeline-Status**: 
- [![CI - Build and Test](https://github.com/Skifai/event-management-system-tech-prototyp/actions/workflows/ci.yml/badge.svg)](https://github.com/Skifai/event-management-system-tech-prototyp/actions/workflows/ci.yml)
- [![Package - Create JAR Artifact](https://github.com/Skifai/event-management-system-tech-prototyp/actions/workflows/package.yml/badge.svg)](https://github.com/Skifai/event-management-system-tech-prototyp/actions/workflows/package.yml)

### Workflows
1. **CI Workflow** (`ci.yml`) - Build & Test bei jedem Push/PR
2. **Package Workflow** (`package.yml`) - JAR-Erstellung für `main` und `develop`
3. **Docker Dev Workflow** (`docker-dev.yml`) - Development Docker Images (optional, deaktiviert)
4. **Docker Prod Workflow** (`docker-prod.yml`) - Production Native Images (optional, deaktiviert)

### Automatische Build & Test
- Wird bei jedem Push und Pull Request ausgeführt
- Verwendet H2 in-memory Database für Tests (keine PostgreSQL-Abhängigkeit)
- Generiert JaCoCo Test-Coverage Reports
- Erstellt JAR-Artefakte für `main` und `develop` Branches

**Detaillierte Informationen**: Siehe [CI-CD.md](CI-CD.md) für vollständige Pipeline-Dokumentation

## Dokumentation

- [DOCKER.md](DOCKER.md) - Docker-spezifische Anweisungen
- [CI-CD.md](CI-CD.md) - CI/CD Pipeline Dokumentation
- [IMPLEMENTATION_SUMMARY.md](IMPLEMENTATION_SUMMARY.md) - Vollständige Implementierungsübersicht
- JavaDoc in Quellcode
- Architektur-Diagramme: (TBD)

## Lizenz

[Add your license information here]

## Autoren

Entwickelt als technischer Prototyp für das Event Management System des Flossrennens.
