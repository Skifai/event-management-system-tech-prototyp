# IntelliJ IDEA Setup und Verwendung

Dieses Dokument beschreibt die Einrichtung und Verwendung des Event Management Systems in IntelliJ IDEA.

## Wichtige Änderungen (November 2024)

✅ **Unabhängige Container-Verwaltung**
- Datenbank-Container werden jetzt **unabhängig** von Run Configurations verwaltet
- Neue Run Configurations: "Start Databases" und "Stop Databases"
- Run Configurations erstellen **keine** Container mehr automatisch
- Bessere Kontrolle über die Container-Lebensdauer

✅ **Spring Boot Application Run Configurations**
- Verwendet native IDEA Spring Boot Integration (statt Maven)
- Profile werden über IDEA's `Active profiles` Feld gesetzt
- Bessere Integration mit IDEA's Spring Boot Tools (Dashboard, Beans View, etc.)
- Schnellerer Start ohne Maven-Overhead

✅ **Drei Modi verfügbar**:
1. **Development Mode**: Für tägliche Entwicklung
2. **Production Mode (Local)**: Schnelles Testen von Prod-Settings ohne Docker
3. **Production Mode (Docker)**: Vollständiger Stack mit GraalVM Native Image

## Voraussetzungen

- IntelliJ IDEA (Community oder Ultimate Edition)
- Java 21
- Docker Desktop oder Docker Engine (muss laufen)
- Git

## Schnellstart

### Erstmalige Einrichtung

1. Docker Desktop starten
2. Projekt in IDEA öffnen
3. **Wichtig**: Datenbank-Container starten
   - Run Configuration "Start Databases" ausführen (▶️)
   - ODER Kommandozeile: `docker compose -f docker-compose.db.yml up -d`
4. "Development Mode" Run Configuration auswählen
5. Run klicken (▶️) oder `Shift+F10`
6. Fertig! Browser öffnet automatisch http://localhost:8080

### Tägliche Nutzung

1. Docker Desktop läuft bereits
2. Datenbank-Container laufen bereits (werden beim ersten Mal gestartet und bleiben aktiv)
3. "Development Mode" auswählen und starten
4. Entwickeln!

> **Hinweis**: Die Datenbank-Container müssen nur einmal gestartet werden und bleiben dann im Hintergrund aktiv. Sie müssen sie nicht bei jedem Start der Anwendung neu starten.

## Projekt Setup

### 1. Repository klonen

```bash
git clone https://github.com/Skifai/event-management-system-tech-prototyp.git
cd event-management-system-tech-prototyp
```

### 2. Projekt in IDEA öffnen

1. IntelliJ IDEA starten
2. `File` → `Open...`
3. Projektverzeichnis auswählen und öffnen
4. IDEA importiert automatisch das Maven-Projekt
5. Warten bis Maven-Dependencies heruntergeladen sind

### 3. Java 21 konfigurieren (falls nötig)

1. `File` → `Project Structure` → `Project`
2. SDK: Java 21 auswählen (oder über `Add SDK` → `Download JDK` installieren)
3. Language Level: `21 - Pattern matching for switch`
4. `Apply` und `OK`

## Run Configurations

### 0. Start Databases (Voraussetzung) 🔧

**Für**: Container-Management (muss vor allen anderen Modi ausgeführt werden)

**Typ**: Shell Script  
**Zweck**: Startet beide PostgreSQL-Container unabhängig

**Start**:
1. Docker Desktop läuft
2. "Start Databases" auswählen
3. Run (▶️)

**Was passiert**:
- Startet Development DB Container (Port 5432)
- Startet Production DB Container (Port 5433)
- Container bleiben im Hintergrund aktiv
- Müssen nur einmal gestartet werden

**Container Details**:
```
Development DB:
  - Container: event-management-db-container
  - Port: 5432
  - Database: eventmanagement
  - User/Pass: postgres/postgres

Production DB:
  - Container: event-management-db-prod-container  
  - Port: 5433
  - Database: eventmanagement_prod
  - User/Pass: postgres/postgres
```

**Wichtig**: 
- ✅ Diese Container bleiben aktiv und müssen nicht bei jedem App-Start neu gestartet werden
- ✅ Sie überleben Neustarts von IDEA und dem Computer (sofern Docker läuft)
- ⚠️ Run Configurations versuchen NICHT mehr, Container automatisch zu erstellen

### 1. Development Mode ⭐ (Empfohlen)

**Für**: Tägliche Entwicklung

**Typ**: Spring Boot Application  
**Profile**: `dev`  
**Port**: 8080  
**DB Port**: 5432

**Voraussetzungen**:
1. Docker Desktop läuft
2. **Datenbank-Container sind gestartet** (siehe "Start Databases")

**Start**:
1. Voraussetzungen erfüllt
2. "Development Mode" auswählen
3. Run (▶️) oder `Shift+F10`

**Features**:
- ✅ Verbindet sich mit PostgreSQL Container auf Port 5432
- ✅ Hot-Reload mit DevTools
- ✅ SQL-Logs für Debugging
- ✅ Browser öffnet automatisch
- ✅ Testdaten werden geladen
- ⚠️ Container wird NICHT automatisch erstellt (muss vorher gestartet sein)

**Datenbank**:
- Container: `event-management-db-container`
- URL: `localhost:5432/eventmanagement`
- User/Pass: `postgres`/`postgres`

### 2. Production Mode (Local) 🟢

**Für**: Schnelles Testen von Production-Settings

**Typ**: Spring Boot Application  
**Profile**: `prod`  
**Port**: 8081  
**DB Port**: 5433

**Voraussetzungen**:
1. Docker Desktop läuft
2. **Datenbank-Container sind gestartet** (siehe "Start Databases")

**Start**:
1. Voraussetzungen erfüllt
2. "Production Mode (Local)" auswählen
3. Run (▶️)

**Features**:
- ✅ Production-Einstellungen (optimiert, minimal logging)
- ✅ Schneller Start (kein Docker-Build)
- ✅ Verbindet sich mit separate DB auf Port 5433
- ⚠️ Container wird NICHT automatisch erstellt (muss vorher gestartet sein)
- ❌ Keine Testdaten

### 3. Production Mode (Docker) 🐳

**Für**: Vollständiger Production-Stack mit GraalVM

**Typ**: Shell Script  
**Port**: 8081  
**DB Port**: 5433

**Start**:
1. "Production Mode (Docker)" auswählen
2. Run (▶️)
3. **Warten** 5-15 Minuten (beim ersten Mal)

**Features**:
- ✅ GraalVM Native Image (optimiert)
- ✅ Docker Compose Setup
- ✅ Separate Production DB (unabhängig von "Start Databases")
- ⚠️ Langer Build beim ersten Mal

**Stoppen**: "Stop Production Mode" Run Configuration

### 4. Build Production JAR 📦

Erstellt production-ready JAR: `target/event-management-system-0.0.1-SNAPSHOT.jar`

### 5. Stop Production Mode 🛑

Stoppt Docker Production Container (nur für Production Mode Docker).

### 6. Stop Databases 🛑

Stoppt beide Datenbank-Container (Dev + Prod).

**Wichtig**: Dies stoppt die Container, die mit "Start Databases" gestartet wurden.

## Übersicht

| Configuration | Profile | Port | DB Port | Container Management | Use Case |
|--------------|---------|------|---------|---------------------|----------|
| Start Databases | - | - | 5432, 5433 | Startet beide DBs | Voraussetzung |
| Development | `dev` | 8080 | 5432 | Benötigt laufende DB | Tägliche Arbeit |
| Prod (Local) | `prod` | 8081 | 5433 | Benötigt laufende DB | Schnelles Prod-Testing |
| Prod (Docker) | `prod` | 8081 | 5433 | Eigene DB im Docker | Vollständiger Stack |
| Stop Databases | - | - | - | Stoppt beide DBs | Aufräumen |

## Spring Profiles

### Profile: `dev`

**File**: `application-dev.properties`

```properties
server.port=8080
spring.datasource.url=jdbc:postgresql://localhost:5432/eventmanagement
spring.jpa.hibernate.ddl-auto=update
vaadin.productionMode=false
logging.level.ch.flossrennen.eventmanagement=DEBUG
app.testdata.enabled=true
```

**Features**:
- Verbindet sich mit Dev-DB Container (Port 5432)
- Hot-Reload mit DevTools
- Verbose Logging
- Automatische Schema-Updates
- Testdaten werden geladen

**Wichtig**: Container muss vorher mit "Start Databases" gestartet werden!

### Profile: `prod`

**File**: `application-prod.properties`

```properties
server.port=8081
spring.datasource.url=jdbc:postgresql://localhost:5433/eventmanagement_prod
spring.jpa.hibernate.ddl-auto=validate
vaadin.productionMode=true
logging.level.ch.flossrennen.eventmanagement=INFO
app.testdata.enabled=false
```

**Features**:
- Verbindet sich mit Prod-DB Container (Port 5433)
- DatabaseStartupListener prüft Container-Verfügbarkeit
- Minimal Logging
- Schema nur Validierung
- Keine Testdaten

**Wichtig**: Container muss vorher mit "Start Databases" gestartet werden!

## Warum Spring Boot Application statt Maven?

### Vorteile

✅ **Native IDEA Integration**:
- Spring Boot Dashboard
- Beans View
- Besseres Debugging
- LiveReload Support

✅ **Schneller**:
- Kein Maven-Overhead
- Direkter JVM-Start

✅ **Übersichtlicher**:
- Profile sichtbar in UI
- Klare Konfiguration

✅ **Best Practice**:
- Empfohlen von JetBrains
- Standard in Spring Boot Projekten

## Troubleshooting

### "Docker is not available"
```bash
# Prüfen
docker ps

# Lösung: Docker Desktop starten
```

### "Port 5432 already in use"
```bash
# Prüfen was läuft
lsof -i :5432

# Stoppen
sudo service postgresql stop
# oder
docker stop <container-id>
```

### "Cannot connect to database"

**Symptom**: Application startet, aber kann sich nicht mit der Datenbank verbinden

```bash
# 1. Prüfen ob Container laufen
docker ps | grep event-management-db

# 2. Wenn keine Container laufen, starten Sie sie:
# Über IDEA:
#   - Run Configuration "Start Databases" ausführen
# Oder Kommandozeile:
docker compose -f docker-compose.db.yml up -d

# 3. Container Logs prüfen (wenn Container läuft aber Verbindung fehlschlägt)
docker logs event-management-db-container        # für Dev
docker logs event-management-db-prod-container   # für Prod

# 4. Container neu starten (wenn Logs Probleme zeigen)
docker restart event-management-db-container     # für Dev
docker restart event-management-db-prod-container # für Prod
```

### "PostgreSQL container is not running" beim App-Start

**Symptom**: Fehlermeldung beim Starten der Anwendung

**Lösung**:
1. Datenbank-Container müssen **vor** der Anwendung gestartet werden
2. In IDEA: Run Configuration "Start Databases" ausführen
3. Oder Kommandozeile: `docker compose -f docker-compose.db.yml up -d`
4. Dann Anwendung erneut starten

### "Release version 21 not supported"
1. `File` → `Project Structure` → `Project`
2. SDK: Java 21 auswählen/downloaden
3. Language Level: 21

### Native Build fehlschlägt
1. Docker Desktop → Settings → Resources
2. Memory: Mindestens 4GB
3. Disk: Mindestens 5GB frei

## Development Workflow

### Typischer Tag

1. **Morgens**: 
   - Docker Desktop starten
   - **Erstmals**: Datenbank-Container starten ("Start Databases" Run Config)
   - **Danach**: Container laufen bereits im Hintergrund
2. **Entwickeln**: 
   - Development Mode starten
   - Code ändern, DevTools reloaded automatisch
3. **Debuggen**: Debug-Modus (🐞), Breakpoints setzen
4. **Testen**: `./mvnw test` vor Commit
5. **Production testen**: Optional mit Production Mode (Local)
6. **Abends**: Optional - Container können laufen bleiben oder mit "Stop Databases" gestoppt werden

### Best Practices

✅ **DOs**:
- Datenbank-Container einmal starten, dann laufen lassen
- Development Mode für tägliche Arbeit
- DevTools Hot-Reload nutzen
- SQL-Logs beobachten
- Regelmäßig testen

❌ **DON'Ts**:
- Nicht vergessen, Datenbank-Container vor App-Start zu starten
- Nicht in Production Mode entwickeln
- Nicht beide Modi gleichzeitig auf gleichen Ports
- Nicht Dev-DB für Prod verwenden

## Database Management

### Container Management

```bash
# Alle Datenbank-Container starten
docker compose -f docker-compose.db.yml up -d

# Container Status prüfen
docker compose -f docker-compose.db.yml ps

# Logs anzeigen
docker compose -f docker-compose.db.yml logs -f

# Container stoppen
docker compose -f docker-compose.db.yml down

# Container stoppen und Daten löschen
docker compose -f docker-compose.db.yml down -v
```

### Development DB
```bash
# Verbinden
docker exec -it event-management-db-container psql -U postgres -d eventmanagement

# Backup
docker exec event-management-db-container pg_dump -U postgres eventmanagement > backup.sql

# Restore
cat backup.sql | docker exec -i event-management-db-container psql -U postgres -d eventmanagement

# Reset
docker exec event-management-db-container psql -U postgres -c "DROP DATABASE eventmanagement;"
docker exec event-management-db-container psql -U postgres -c "CREATE DATABASE eventmanagement;"
```

### Production DB
```bash
# Verbinden
docker exec -it event-management-db-prod-container psql -U postgres -d eventmanagement_prod

# Backup
docker exec event-management-db-prod-container pg_dump -U postgres eventmanagement_prod > backup_prod.sql

# Restore
cat backup_prod.sql | docker exec -i event-management-db-prod-container psql -U postgres -d eventmanagement_prod
```

## Weitere Ressourcen

- **Run Configurations Details**: [.idea/runConfigurations/README.md](.idea/runConfigurations/README.md)
- **Hauptdokumentation**: [README.md](README.md)
- **Docker Dokumentation**: [DOCKER.md](DOCKER.md)
- **CI/CD Pipeline**: [CI-CD.md](CI-CD.md)

## Support

Bei Problemen bitte ein GitHub Issue erstellen mit:
- Fehlermeldung
- Run Configuration Name
- `docker --version`
- `java -version`
- OS und IntelliJ IDEA Version

---

**Zusammenfassung**: Die neuen Run Configurations verwenden native Spring Boot Application Konfigurationen für bessere IDEA-Integration. Datenbank-Container werden jetzt **unabhängig** verwaltet - starten Sie sie einmal mit "Start Databases", dann können Sie die Anwendung beliebig oft starten und stoppen! 🎉

## Wichtige Änderungen gegenüber vorher

### Was ist neu?

✅ **Unabhängige Container-Verwaltung**:
- Container werden nicht mehr automatisch von Run Configurations erstellt
- Neue Run Configurations "Start Databases" und "Stop Databases" 
- Container bleiben im Hintergrund aktiv
- Bessere Kontrolle über Container-Lebensdauer

✅ **Neue docker-compose.db.yml**:
- Verwaltet beide Datenbank-Container (Dev + Prod)
- Kann manuell oder über IDEA Run Configurations verwendet werden

### Warum diese Änderung?

🎯 **Bessere Trennung von Concerns**:
- Anwendung verwaltet keine Container mehr
- Container-Lifecycle ist unabhängig von App-Lifecycle
- Einfacher zu verstehen und zu debuggen

🎯 **Flexibler**:
- Container können geteilt werden zwischen mehreren App-Instanzen
- Einfacher für lokale Entwicklung mit mehreren Projekten
- Container können laufen bleiben zwischen IDEA-Neustarts

🎯 **Weniger Überraschungen**:
- Klare Fehlermeldungen wenn Container nicht laufen
- Keine automatische Container-Erstellung mehr
- Vorhersehbares Verhalten
