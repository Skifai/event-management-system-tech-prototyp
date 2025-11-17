# IntelliJ IDEA Setup und Verwendung

Dieses Dokument beschreibt die Einrichtung und Verwendung des Event Management Systems in IntelliJ IDEA.

## Automatische Funktionen

Das Projekt bietet folgende automatische Funktionen für die Entwicklung in IntelliJ IDEA:

1. **Automatische PostgreSQL Docker-Instanz**: Die Applikation prüft beim Start, ob eine PostgreSQL Docker-Instanz verfügbar ist und erstellt bei Bedarf automatisch eine neue.
2. **Produktionsmodus**: Möglichkeit die Applikation im Production Modus mit GraalVM Native Image zu kompilieren und als Container zu starten.
3. **Vorkonfigurierte Run Configurations**: Alle benötigten Run Configurations sind im Repository enthalten.

## Voraussetzungen

- IntelliJ IDEA (Community oder Ultimate Edition)
- Java 21 (wird automatisch von IDEA verwendet, falls installiert)
- Docker Desktop oder Docker Engine (muss laufen)
- Git

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

### 3. Java 21 konfigurieren (falls noch nicht geschehen)

1. `File` → `Project Structure` → `Project`
2. SDK: Java 21 auswählen (oder über `Add SDK` → `Download JDK` installieren)
3. Language Level: `21 - Pattern matching for switch` auswählen

## Run Configurations

Nach dem Öffnen des Projekts stehen automatisch folgende Run Configurations zur Verfügung:

### 1. Development Mode ⭐ (Empfohlen für Entwicklung)

**Zweck**: Lokale Entwicklung mit automatischem PostgreSQL-Management

**Spring Profile**: Keines (Standard) oder `dev`

**Was passiert beim Start**:
1. Die Applikation prüft, ob Docker verfügbar ist
2. Sucht nach PostgreSQL Container `event-management-db-container`
3. Falls nicht vorhanden: Erstellt und startet neuen PostgreSQL Container
4. Falls vorhanden aber gestoppt: Startet den Container
5. Falls bereits laufend: Verwendet bestehenden Container
6. Startet die Spring Boot Applikation mit DevTools
7. Verwendet `application.properties` (Standard-Konfiguration)

**Verwendung**:
1. Run Configuration "Development Mode" auswählen
2. Run (▶️) oder Debug (🐞) klicken (Shift+F10 / Shift+F9)
3. Applikation öffnet sich automatisch im Browser: http://localhost:8080

**Konfiguration (application.properties)**:
- Server Port: `8080`
- Vaadin: `productionMode=false` (Development-Mode)
- Logging: `DEBUG` für SQL und Application
- DDL Auto: `update` (Schema-Updates automatisch)
- Browser Launch: Automatisch

**Datenbank-Details**:
- Host: `localhost`
- Port: `5432`
- Database: `eventmanagement`
- Username: `postgres`
- Password: `postgres`

**Vorteile**:
- ✅ Keine manuelle Datenbank-Installation nötig
- ✅ Hot-Reload mit Spring Boot DevTools
- ✅ SQL-Logs für Debugging
- ✅ Automatisches Container-Management

### 2. Production Mode (Native)

**Zweck**: Production-Build mit GraalVM Native Image

**Spring Profile**: `prod`

**Was passiert beim Start**:
1. Baut GraalVM Native Image mit Docker (dauert 5-15 Minuten)
2. Startet separate Production PostgreSQL auf Port 5433
3. Startet Applikation-Container auf Port 8081
4. Verwendet `application-prod.properties` (Production-Konfiguration)
5. Setzt Environment Variable: `SPRING_PROFILES_ACTIVE=prod`

**Verwendung**:
1. Run Configuration "Production Mode (Native)" auswählen
2. Run klicken
3. Warten bis Build abgeschlossen ist
4. Applikation verfügbar unter: http://localhost:8081

**Konfiguration (application-prod.properties)**:
- Server Port: `8080` (im Container, mapped auf `8081` auf Host)
- Vaadin: `productionMode=true` (Production-Mode)
- Logging: `INFO` für Application, `WARN` für SQL
- DDL Auto: `validate` (nur Schema-Validierung, keine Updates!)
- Browser Launch: Deaktiviert

**Wichtig**:
- ⚠️ Erster Build dauert lange (5-15 Minuten)
- ⚠️ Benötigt mindestens 4GB RAM für Docker
- ✅ Nachfolgende Builds sind schneller (Docker Cache)
- ✅ Verwendet separate Production-Datenbank
- ⚠️ DatabaseStartupListener ist **deaktiviert** im prod-Profil

**Production-Datenbank-Details**:
- Host: `localhost`
- Port: `5433` (nicht 5432!)
- Database: `eventmanagement_prod`
- Username: `postgres`
- Password: `postgres`

### 3. Build Production JAR

**Zweck**: JAR-File für Production bauen (ohne Container)

**Verwendung**:
1. Run Configuration "Build Production JAR" auswählen
2. Run klicken
3. JAR-File wird erstellt: `target/event-management-system-0.0.1-SNAPSHOT.jar`

**Maven Profile**: Aktiviert automatisch `production` Profile für optimierte Vaadin-Frontend-Kompilierung.

### 4. Stop Production Mode

**Zweck**: Production-Container stoppen und entfernen

**Verwendung**:
1. Run Configuration "Stop Production Mode" auswählen
2. Run klicken
3. Alle Production-Container werden gestoppt

**Hinweis**: Daten bleiben in Docker Volume erhalten.

## Troubleshooting

### Problem: "Docker is not available"

**Lösung**:
1. Docker Desktop starten
2. Terminal öffnen und testen: `docker ps`
3. Falls Fehler: Docker neu installieren oder Berechtigungen prüfen

### Problem: "Port 5432 already in use"

**Ursache**: Anderer PostgreSQL-Service oder Container läuft bereits.

**Lösung**:
```bash
# Prüfen was auf Port 5432 läuft
lsof -i :5432

# Option 1: Anderen Service stoppen
sudo service postgresql stop

# Option 2: Container entfernen
docker stop event-management-db-container
docker rm event-management-db-container
```

### Problem: "Cannot connect to database"

**Lösung**:
1. Prüfen ob Container läuft: `docker ps`
2. Container-Logs prüfen: `docker logs event-management-db-container`
3. Warten (PostgreSQL benötigt ~5 Sekunden zum Starten)
4. Application neu starten

### Problem: "Release version 21 not supported"

**Ursache**: Java 21 ist nicht installiert oder nicht konfiguriert.

**Lösung**:
1. `File` → `Project Structure` → `Project`
2. SDK: Java 21 auswählen
3. Falls nicht verfügbar: `Add SDK` → `Download JDK` → `21` auswählen

### Problem: Native Build schlägt fehl

**Häufige Ursachen**:
- Nicht genug Speicherplatz (mindestens 5GB frei)
- Nicht genug RAM für Docker (mindestens 4GB)

**Lösung**:
1. Docker Desktop → Settings → Resources
2. Memory auf mindestens 4GB erhöhen
3. Disk Image Size prüfen

## Entwicklungs-Workflow

### Typischer Entwicklungsablauf

1. **Start**:
   - "Development Mode" Run Configuration starten
   - Warten bis Applikation gestartet ist
   - Browser öffnet sich automatisch

2. **Entwickeln**:
   - Code ändern
   - Bei Java-Änderungen: Automatischer Reload durch DevTools
   - Bei Frontend-Änderungen: Browser refreshen

3. **Debuggen**:
   - Debug-Modus verwenden (🐞)
   - Breakpoints setzen
   - SQL-Logs in Console beobachten

4. **Testen**:
   - Vor Commit: Tests laufen lassen
   ```bash
   ./mvnw test
   ```

5. **Production-Test** (optional):
   - "Production Mode (Native)" starten
   - Production-Build testen
   - "Stop Production Mode" ausführen

### Best Practices

#### Development

✅ **DOs**:
- Development Mode für tägliche Arbeit verwenden
- DevTools Hot-Reload nutzen
- SQL-Logs für Debugging aktiviert lassen
- Regelmäßig Tests ausführen

❌ **DON'Ts**:
- Nicht direkt in Production Mode entwickeln (zu langsam)
- Nicht beide Modi gleichzeitig laufen lassen (Port-Konflikte)
- Nicht Development-DB für Production verwenden

#### Database Management

**Development Database**:
```bash
# Verbinden
docker exec -it event-management-db-container psql -U postgres -d eventmanagement

# Backup erstellen
docker exec event-management-db-container pg_dump -U postgres eventmanagement > backup.sql

# Backup wiederherstellen
cat backup.sql | docker exec -i event-management-db-container psql -U postgres -d eventmanagement

# Datenbank zurücksetzen
docker exec event-management-db-container psql -U postgres -c "DROP DATABASE eventmanagement;"
docker exec event-management-db-container psql -U postgres -c "CREATE DATABASE eventmanagement;"
```

**Production Database**:
```bash
# Verbinden
docker exec -it event-management-db-prod-container psql -U postgres -d eventmanagement_prod

# Ähnlich wie Development, aber mit "-prod" Container-Namen
```

## Spring Profiles Übersicht

Das Projekt verwendet Spring Profiles um verschiedene Konfigurationen zu verwalten:

### Verfügbare Profile

#### 1. Standard / Development (kein Profil oder `dev`)

**Aktivierung:**
- Automatisch wenn kein Profil gesetzt ist
- Explizit via: `-Dspring.profiles.active=dev`
- Run Configuration: "Development Mode"

**Properties-Datei:** `application.properties`

**Eigenschaften:**
```properties
# Server
server.port=8080

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/eventmanagement
spring.jpa.hibernate.ddl-auto=update

# Vaadin
vaadin.productionMode=false
vaadin.launch-browser=true

# Logging
logging.level.ch.flossrennen.eventmanagement=DEBUG
logging.level.org.hibernate.SQL=DEBUG
```

**Besonderheiten:**
- ✅ DatabaseStartupListener **aktiv** (Auto-Start PostgreSQL)
- ✅ DevTools für Hot-Reload
- ✅ SQL-Logs aktiviert
- ✅ Schema-Updates automatisch

---

#### 2. Production (`prod`)

**Aktivierung:**
- Via: `-Dspring.profiles.active=prod`
- Via Environment Variable: `SPRING_PROFILES_ACTIVE=prod`
- Run Configuration: "Production Mode (Native)"
- Docker Compose: `docker-compose.prod.yml`

**Properties-Datei:** `application-prod.properties`

**Eigenschaften:**
```properties
# Server
server.port=8080  # Im Container, Host-Port ist 8081

# Database
spring.datasource.url=jdbc:postgresql://localhost:5433/eventmanagement_prod
spring.jpa.hibernate.ddl-auto=validate

# Vaadin
vaadin.productionMode=true
vaadin.launch-browser=false

# Logging
logging.level.ch.flossrennen.eventmanagement=INFO
logging.level.org.hibernate.SQL=WARN
```

**Besonderheiten:**
- ❌ DatabaseStartupListener **inaktiv** (kein Auto-Start)
- ❌ Keine DevTools
- ❌ Minimale Logs
- ⚠️ Schema nur validiert, nicht aktualisiert

---

### Profile in IDEA setzen

**Option 1: Via Run Configuration (empfohlen)**

1. Run → Edit Configurations...
2. Spring Boot Configuration auswählen
3. `Active profiles` Feld: `prod` (oder leer für Standard)
4. Alternativ unter `Environment variables`: `SPRING_PROFILES_ACTIVE=prod`

**Option 2: Via VM Options**

In Run Configuration unter `VM options`:
```
-Dspring.profiles.active=prod
```

**Option 3: Via Environment Variable**

In Run Configuration unter `Environment variables`:
```
SPRING_PROFILES_ACTIVE=prod
```

---

### Eigenes Profil erstellen

**Schritt 1:** Neue Properties-Datei erstellen

```bash
touch src/main/resources/application-mein-profil.properties
```

**Schritt 2:** Konfiguration anpassen

```properties
# Beispiel: Test-Profil mit H2 In-Memory DB
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.hibernate.ddl-auto=create-drop
```

**Schritt 3:** In IDEA aktivieren

Run Configuration erstellen mit `Active profiles: mein-profil`

---

### Profile-Spezifische Beans

Sie können Beans nur für bestimmte Profile aktivieren:

```java
@Service
@Profile("dev")
public class DevOnlyService {
    // Nur im Development-Profil aktiv
}

@Service
@Profile("prod")
public class ProductionService {
    // Nur im Production-Profil aktiv
}

@Service
@Profile("!prod")  // Negation
public class NotInProductionService {
    // In allen Profilen außer prod
}
```

**Beispiel im Projekt:**

```java
@Component
public class DatabaseStartupListener {
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        // Nur aktiv wenn NICHT prod-Profil
        if (Arrays.asList(environment.getActiveProfiles()).contains("prod")) {
            return;
        }
        dockerService.ensurePostgresContainer();
    }
}
```

---

## Environment Variables

### Development Mode

Standardwerte können in der Run Configuration oder via Environment Variables überschrieben werden:

```properties
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/eventmanagement
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
SPRING_JPA_HIBERNATE_DDL_AUTO=update
```

### Production Mode

Konfiguration über `.env` File oder `docker-compose.prod.yml`:

```properties
POSTGRES_USER=postgres
POSTGRES_PASSWORD=changeme_in_production
POSTGRES_DB=eventmanagement_prod
```

## Weitere Ressourcen

- **Run Configurations Details**: [.idea/runConfigurations/README.md](.idea/runConfigurations/README.md)
- **Docker Dokumentation**: [DOCKER.md](DOCKER.md)
- **CI/CD Pipeline**: [CI-CD.md](CI-CD.md)
- **Hauptdokumentation**: [README.md](README.md)

## Support

Bei Problemen:
1. Logs prüfen (Console in IDEA)
2. Docker Container Status prüfen: `docker ps -a`
3. Container Logs prüfen: `docker logs <container-name>`
4. Issue auf GitHub erstellen mit:
   - Fehlermeldung
   - Run Configuration Name
   - Docker Version: `docker --version`
   - Java Version: `java -version`
   - OS Version
