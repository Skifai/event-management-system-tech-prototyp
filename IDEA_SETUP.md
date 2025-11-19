# IntelliJ IDEA Setup und Verwendung

Dieses Dokument beschreibt die Einrichtung und Verwendung des Event Management Systems in IntelliJ IDEA mit den neuen, verbesserten Run Configurations.

## Wichtige Änderungen (November 2024)

✅ **Neue Spring Boot Application Run Configurations**
- Verwendet jetzt native IDEA Spring Boot Integration (statt Maven)
- Profile werden über IDEA's `Active profiles` Feld gesetzt
- Bessere Integration mit IDEA's Spring Boot Tools (Dashboard, Beans View, etc.)
- Schnellerer Start ohne Maven-Overhead

✅ **Drei Modi verfügbar**:
1. **Development Mode**: Für tägliche Entwicklung mit Auto-PostgreSQL
2. **Production Mode (Local)**: Schnelles Testen von Prod-Settings ohne Docker
3. **Production Mode (Docker)**: Vollständiger Stack mit GraalVM Native Image

## Voraussetzungen

- IntelliJ IDEA (Community oder Ultimate Edition)
- Java 21
- Docker Desktop oder Docker Engine (muss laufen)
- Git

## Schnellstart

1. Docker Desktop starten
2. Projekt in IDEA öffnen
3. "Development Mode" Run Configuration auswählen
4. Run klicken (▶️) oder `Shift+F10`
5. Fertig! Browser öffnet automatisch http://localhost:8080

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

### 1. Development Mode ⭐ (Empfohlen)

**Für**: Tägliche Entwicklung

**Typ**: Spring Boot Application  
**Profile**: `dev`  
**Port**: 8080  
**DB Port**: 5432

**Start**:
1. Docker Desktop läuft
2. "Development Mode" auswählen
3. Run (▶️) oder `Shift+F10`

**Features**:
- ✅ PostgreSQL Container wird automatisch gestartet/erstellt
- ✅ Hot-Reload mit DevTools
- ✅ SQL-Logs für Debugging
- ✅ Browser öffnet automatisch
- ✅ Testdaten werden geladen

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

**Start**:
1. Production DB manuell starten:
   ```bash
   docker run -d \
     --name event-management-db-prod-container \
     -e POSTGRES_DB=eventmanagement_prod \
     -e POSTGRES_USER=postgres \
     -e POSTGRES_PASSWORD=postgres \
     -p 5433:5432 \
     postgres:17-alpine
   ```
2. "Production Mode (Local)" auswählen
3. Run (▶️)

**Features**:
- ✅ Production-Einstellungen (optimiert, minimal logging)
- ✅ Schneller Start (kein Docker-Build)
- ✅ Separate DB auf Port 5433
- ❌ Kein Auto-Start der DB
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
- ✅ Separate Production DB
- ⚠️ Langer Build beim ersten Mal

**Stoppen**: "Stop Production Mode" Run Configuration

### 4. Build Production JAR 📦

Erstellt production-ready JAR: `target/event-management-system-0.0.1-SNAPSHOT.jar`

### 5. Stop Production Mode 🛑

Stoppt Docker Production Container.

## Übersicht

| Configuration | Profile | Port | DB Port | Auto-DB | Use Case |
|--------------|---------|------|---------|---------|----------|
| Development | `dev` | 8080 | 5432 | ✅ | Tägliche Arbeit |
| Prod (Local) | `prod` | 8081 | 5433 | ❌ | Schnelles Prod-Testing |
| Prod (Docker) | `prod` | 8081 | 5433 | ✅ | Vollständiger Stack |

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
- Auto-Start PostgreSQL via DatabaseStartupListener
- Hot-Reload mit DevTools
- Verbose Logging
- Automatische Schema-Updates
- Testdaten werden geladen

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
- DatabaseStartupListener deaktiviert
- Minimal Logging
- Schema nur Validierung
- Keine Testdaten

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
```bash
# Container Status
docker ps

# Logs prüfen
docker logs event-management-db-container

# Container starten
docker start event-management-db-container
```

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

1. **Morgens**: Docker starten, Development Mode laufen lassen
2. **Entwickeln**: Code ändern, DevTools reloaded automatisch
3. **Debuggen**: Debug-Modus (🐞), Breakpoints setzen
4. **Testen**: `./mvnw test` vor Commit
5. **Production testen**: Optional mit Production Mode (Local)

### Best Practices

✅ **DOs**:
- Development Mode für tägliche Arbeit
- DevTools Hot-Reload nutzen
- SQL-Logs beobachten
- Regelmäßig testen

❌ **DON'Ts**:
- Nicht in Production Mode entwickeln
- Nicht beide Modi gleichzeitig
- Nicht Dev-DB für Prod verwenden

## Database Management

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
# Manuell starten (für Local Mode)
docker run -d \
  --name event-management-db-prod-container \
  -e POSTGRES_DB=eventmanagement_prod \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5433:5432 \
  postgres:17-alpine

# Verbinden
docker exec -it event-management-db-prod-container psql -U postgres -d eventmanagement_prod
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

**Zusammenfassung**: Die neuen Run Configurations verwenden native Spring Boot Application Konfigurationen für bessere IDEA-Integration. Einfach "Development Mode" auswählen, Run klicken, fertig! 🎉
