# Anleitung: Event Management System starten

Dieses Dokument beschreibt Schritt für Schritt, wie Sie das Event Management System starten können.

## Übersicht der Start-Optionen

Es gibt drei Hauptmethoden, um das System zu starten:

1. **Docker Compose** (empfohlen für Production, einfachster Start)
2. **IntelliJ IDEA** (empfohlen für Entwicklung)
3. **Kommandozeile** (Maven + PostgreSQL)

## Option 1: Docker Compose (Empfohlen)

Dies ist die einfachste Methode für einen vollständigen Production-Start mit GraalVM Native Image.

### Voraussetzungen
- [ ] Docker Desktop installiert und läuft
- [ ] Git installiert (zum Klonen des Repositories)

### Schritte

```bash
# 1. Repository klonen (falls noch nicht geschehen)
git clone <repository-url>
cd event-management-system-tech-prototyp

# 2. Container starten (baut automatisch das GraalVM Native Image)
docker compose up --build
```

**Wichtig:**
- Der erste Build dauert 5-15 Minuten (GraalVM Native Image Kompilierung)
- Nachfolgende Starts sind deutlich schneller
- Die Anwendung ist unter http://localhost:8080 erreichbar
- PostgreSQL läuft auf Port 5432

**Container stoppen:**
```bash
docker compose down

# Mit Datenlöschung:
docker compose down -v
```

## Option 2: IntelliJ IDEA (Empfohlen für Entwicklung)

Für Entwicklung mit Hot-Reload und Debugging-Support.

### Voraussetzungen
- [ ] IntelliJ IDEA (Community oder Ultimate)
- [ ] Docker Desktop läuft
- [ ] Java 21 in IDEA konfiguriert
- [ ] Projekt in IDEA geöffnet und Maven-Import abgeschlossen

### Erstmalige Einrichtung

#### Schritt 1: Docker Desktop starten

Prüfen Sie, ob Docker läuft:
```bash
docker ps
```

#### Schritt 2: Datenbank-Container starten

**In IntelliJ IDEA:**
1. Run Configuration Dropdown öffnen (oben rechts)
2. **"Start Databases"** auswählen
3. Run-Button (▶️) klicken

**Alternative über Kommandozeile:**
```bash
docker compose -f docker-compose.db.yml up -d
```

**Was passiert:**
- Zwei PostgreSQL Container werden gestartet:
  - Development DB auf Port 5432
  - Production DB auf Port 5433
- Container laufen im Hintergrund
- Bleiben aktiv, auch wenn IDEA geschlossen wird

**Container-Status prüfen:**
```bash
docker ps | grep event-management-db
```

#### Schritt 3: Anwendung starten

1. Run Configuration **"Development Mode"** auswählen
2. Run-Button (▶️) klicken oder `Shift+F10`
3. Warten bis "Started EventManagementSystemApplication" erscheint
4. Browser öffnet automatisch: http://localhost:8080

**Fertig!** Die Anwendung läuft jetzt mit:
- Hot-Reload (Spring Boot DevTools)
- SQL Debug-Logs
- Automatisch geladenen Testdaten
- Vaadin Development Mode

### Tägliche Nutzung

Nach der erstmaligen Einrichtung:

1. **Docker Desktop starten** (falls nicht läuft)
2. **Container prüfen** (optional - sollten noch laufen):
   ```bash
   docker ps | grep event-management-db
   ```
3. **Anwendung starten**: "Development Mode" Run Configuration

### Debugging

1. Debug-Button (🐞) statt Run-Button verwenden
2. Breakpoints setzen
3. Debugging wie gewohnt in IDEA

### Production Mode lokal testen

Um Production-Einstellungen zu testen:

1. Run Configuration **"Production Mode (Local)"** auswählen
2. Run-Button klicken
3. App verfügbar auf: http://localhost:8081 (anderer Port!)

**Unterschiede:**
- Port 8081 statt 8080
- Production-DB auf Port 5433
- Kein Hot-Reload
- Minimales Logging
- Keine Testdaten

## Option 3: Kommandozeile mit Maven

Für lokale Entwicklung ohne IDEA.

### Voraussetzungen
- [ ] Java 21 installiert
- [ ] Maven installiert (oder nutzen Sie `./mvnw`)
- [ ] Docker Desktop läuft

### Schritte

```bash
# 1. Datenbank starten
docker compose -f docker-compose.db.yml up -d

# 2. Anwendung im Development Mode starten
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

Die Anwendung ist dann unter http://localhost:8080 erreichbar.

**Production Build erstellen:**
```bash
# JAR mit Vaadin Production Build
./mvnw clean package -Pproduction

# JAR ausführen
java -jar -Dspring.profiles.active=prod target/event-management-system-*.jar
```

## Verfügbare Profile

Das System unterstützt zwei Profile:

| Profil | Port | Datenbank | Testdaten | Hot-Reload | Verwendung |
|--------|------|-----------|-----------|------------|------------|
| **dev** | 8080 | localhost:5432 | ✅ Ja | ✅ Ja | Entwicklung |
| **prod** | 8081 | localhost:5433 | ❌ Nein | ❌ Nein | Production |

## Testdaten

Im **Development Mode** werden automatisch Testdaten geladen:
- 5 Ressorts (Küche, Bar, Sicherheit, Technik, Dekoration)
- 15 Helfer mit realistischen Daten
- 3 Schichten (Morgen, Mittag, Abend)
- 13 Einsätze über alle Schichten verteilt

Im **Production Mode** sind Testdaten deaktiviert.

## Häufige Probleme

### "PostgreSQL container is not running"

**Lösung:**
```bash
# Container-Status prüfen
docker ps | grep event-management-db

# Falls nicht läuft, Container starten
docker compose -f docker-compose.db.yml up -d

# Anwendung neu starten
```

### "Port 5432 already in use"

**Lösung:**
```bash
# Prüfen was Port verwendet
# Windows:
netstat -ano | findstr :5432

# Linux/Mac:
lsof -i :5432

# Lokalen PostgreSQL stoppen (falls installiert)
# Windows: Service Manager → PostgreSQL stoppen
# Linux: sudo service postgresql stop
# Mac: brew services stop postgresql
```

### "Docker is not available"

**Lösung:**
1. Docker Desktop starten
2. Warten bis vollständig gestartet
3. Mit `docker ps` testen
4. Container und App neu starten

### Container läuft, aber Verbindung schlägt fehl

**Lösung:**
```bash
# Container-Logs prüfen
docker logs event-management-db-container

# Container neu starten
docker restart event-management-db-container

# 5-10 Sekunden warten, dann App neu starten
```

### Erster Docker Build dauert sehr lange

**Normal!** Der erste Build mit GraalVM Native Image dauert 5-15 Minuten. Dies ist eine einmalige Kompilierung. Nachfolgende Starts sind in unter 1 Sekunde.

## Datenbank-Zugriff

### Development Database (Port 5432)

**Via Docker:**
```bash
docker exec -it event-management-db-container psql -U postgres -d eventmanagement
```

**Via IDEA Database Tool oder DBeaver:**
- Host: `localhost`
- Port: `5432`
- Database: `eventmanagement`
- User: `postgres`
- Password: `postgres`

### Production Database (Port 5433)

**Via Docker:**
```bash
docker exec -it event-management-db-prod-container psql -U postgres -d eventmanagement_prod
```

**Via IDEA Database Tool oder DBeaver:**
- Host: `localhost`
- Port: `5433`
- Database: `eventmanagement_prod`
- User: `postgres`
- Password: `postgres`

## IntelliJ IDEA Run Configurations

Übersicht der verfügbaren Run Configurations:

| Run Configuration | Zweck | Wann verwenden |
|------------------|-------|----------------|
| **Start Databases** | Startet PostgreSQL Container | Einmalig oder nach Neustart |
| **Development Mode** | Startet App im Dev-Modus | Tägliche Entwicklung |
| **Production Mode (Local)** | Startet App im Prod-Modus | Prod-Settings lokal testen |
| **Production Mode (Docker)** | Vollständiger Docker Stack | GraalVM Native Image testen |
| **Stop Databases** | Stoppt DB-Container | Container aufräumen |

Details zu den Run Configurations finden Sie in `.idea/runConfigurations/README.md`.

## Checkliste: Erster Start

Für den ersten Start folgen Sie dieser Checkliste:

**Docker Compose Methode:**
- [ ] Docker Desktop läuft
- [ ] Repository geklont
- [ ] `docker compose up --build` ausgeführt
- [ ] Warten (5-15 Min beim ersten Build)
- [ ] Browser öffnet http://localhost:8080
- [ ] Login-Seite wird angezeigt

**IntelliJ IDEA Methode:**
- [ ] Docker Desktop läuft
- [ ] Projekt in IDEA geöffnet
- [ ] Maven-Import abgeschlossen
- [ ] Java 21 konfiguriert
- [ ] Run Config "Start Databases" ausgeführt
- [ ] Container laufen (`docker ps` prüfen)
- [ ] Run Config "Development Mode" ausgeführt
- [ ] Browser öffnet http://localhost:8080
- [ ] App zeigt Login-Seite

**Herzlichen Glückwunsch!** Das Event Management System läuft! 🎉

## Quick Reference

### Container-Befehle

```bash
# Status prüfen
docker compose -f docker-compose.db.yml ps

# Logs anzeigen
docker compose -f docker-compose.db.yml logs -f

# Container stoppen
docker compose -f docker-compose.db.yml down

# Container und Daten löschen
docker compose -f docker-compose.db.yml down -v

# Container neu starten
docker compose -f docker-compose.db.yml restart
```

### Maven-Befehle

```bash
# Development Mode
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Production JAR bauen
./mvnw clean package -Pproduction

# Tests ausführen
./mvnw test

# Test Coverage Report
./mvnw clean test jacoco:report
```

### URL-Übersicht

- **Development**: http://localhost:8080
- **Production (lokal)**: http://localhost:8081
- **Health Check**: http://localhost:8080/actuator/health
- **PostgreSQL Dev**: localhost:5432
- **PostgreSQL Prod**: localhost:5433

## Weitere Dokumentation

- **Haupt-README**: [README.md](README.md) - Vollständige Projektdokumentation
- **IntelliJ Run Configs**: `.idea/runConfigurations/README.md` - Detaillierte Run Configuration Dokumentation

## Support

Bei Problemen:
1. Prüfen Sie die "Häufige Probleme" Sektion oben
2. Prüfen Sie die Container-Logs: `docker logs event-management-db-container`
3. Prüfen Sie die Anwendungs-Logs in IDEA oder Terminal

**Alles zurücksetzen:**
```bash
# Alle Container stoppen und Daten löschen
docker compose -f docker-compose.db.yml down -v
docker compose down -v

# Container neu starten
docker compose -f docker-compose.db.yml up -d

# Anwendung neu starten
```
