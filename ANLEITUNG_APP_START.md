# Anleitung: App in IntelliJ IDEA starten

Dieses Dokument beschreibt Schritt für Schritt, wie Sie das Event Management System in IntelliJ IDEA starten.

## Übersicht

Mit den neuen Run Configurations wird das Starten der Anwendung in IDEA deutlich einfacher:

1. **Container-Verwaltung ist unabhängig**: Datenbank-Container werden separat von der Anwendung verwaltet
2. **Ein-Klick-Start**: Nach erstmaliger Einrichtung nur noch ein Klick zum Starten
3. **Keine automatische Container-Erstellung**: Klare Kontrolle über Container-Lebenszyklus

## Voraussetzungen

Bevor Sie beginnen, stellen Sie sicher, dass folgendes installiert ist:

- [ ] IntelliJ IDEA (Community oder Ultimate Edition)
- [ ] Docker Desktop läuft
- [ ] Java 21 in IDEA konfiguriert
- [ ] Projekt ist in IDEA geöffnet und Maven-Import abgeschlossen

## Erstmalige Einrichtung

### Schritt 1: Docker Desktop starten

Stellen Sie sicher, dass Docker Desktop läuft:

```bash
docker ps
```

Wenn dieser Befehl ohne Fehler ausgeführt wird, ist Docker bereit.

### Schritt 2: Datenbank-Container starten

**Option A: Über IDEA Run Configuration (empfohlen)**

1. In IDEA: Run Configuration Dropdown öffnen (oben rechts)
2. **"Start Databases"** auswählen
3. Run-Button (▶️) klicken
4. Im Terminal erscheint eine Bestätigung mit Container-Details

**Option B: Über Kommandozeile**

```bash
cd /pfad/zum/projekt
docker compose -f docker-compose.db.yml up -d
```

**Was passiert?**
- Development DB Container wird gestartet (Port 5432)
- Production DB Container wird gestartet (Port 5433)
- Container laufen nun im Hintergrund
- Sie bleiben aktiv, auch wenn IDEA geschlossen wird

**Prüfen ob Container laufen:**
```bash
docker ps | grep event-management-db
```

Sie sollten zwei Container sehen:
- `event-management-db-container` (Dev, Port 5432)
- `event-management-db-prod-container` (Prod, Port 5433)

### Schritt 3: Anwendung starten

1. In IDEA: Run Configuration Dropdown öffnen
2. **"Development Mode"** auswählen
3. Run-Button (▶️) klicken **ODER** `Shift+F10` drücken
4. Warten bis "Started EventManagementSystemApplication" im Log erscheint
5. Browser öffnet automatisch: http://localhost:8080

**Fertig!** Die Anwendung läuft jetzt.

## Tägliche Nutzung

Nach der erstmaligen Einrichtung ist es noch einfacher:

### Morgens / Bei erneutem Start

1. **Docker Desktop starten** (falls nicht automatisch gestartet)
2. **Container prüfen** (optional):
   ```bash
   docker ps | grep event-management-db
   ```
   - Wenn Container laufen → direkt zu Schritt 3
   - Wenn Container nicht laufen → Run Config "Start Databases" ausführen
3. **Anwendung starten**: Run Config "Development Mode" ausführen

> 💡 **Tipp**: Die Datenbank-Container müssen normalerweise nur einmal gestartet werden und bleiben dann im Hintergrund aktiv. Sie überleben IDEA-Neustarts und sogar Computer-Neustarts (sofern Docker läuft).

### Entwickeln mit Hot-Reload

Die Development Mode Run Configuration aktiviert Spring Boot DevTools:

- Änderungen an Java-Klassen → Automatischer Reload
- Änderungen an Vaadin Views → Automatischer Reload
- Änderungen an Properties → Manueller Restart erforderlich

Einfach Code ändern, speichern, und die Anwendung aktualisiert sich automatisch!

### Debugging

1. Statt Run-Button (▶️) den Debug-Button (🐞) verwenden
2. Breakpoints in Code setzen
3. Debugger stoppt an Breakpoints
4. Normale Debug-Features von IDEA nutzen

## Production Mode testen

Wenn Sie Production-Einstellungen testen möchten:

1. **Voraussetzung**: Datenbank-Container laufen bereits (siehe Schritt 2 oben)
2. Run Configuration **"Production Mode (Local)"** auswählen
3. Run-Button klicken
4. App verfügbar auf: http://localhost:8081

**Unterschiede zu Development Mode:**
- Läuft auf Port 8081 (statt 8080)
- Verwendet Production-DB auf Port 5433
- Kein Hot-Reload
- Minimal Logging
- Keine Testdaten

## Container stoppen (optional)

Container können laufen bleiben, aber falls Sie sie stoppen möchten:

**Option A: Über IDEA**
- Run Configuration **"Stop Databases"** ausführen

**Option B: Über Kommandozeile**
```bash
docker compose -f docker-compose.db.yml down
```

**Container UND Daten löschen:**
```bash
docker compose -f docker-compose.db.yml down -v
```
⚠️ Dies löscht alle Daten in den Datenbanken!

## Häufige Probleme

### "PostgreSQL container is not running"

**Problem**: Anwendung startet, aber findet keine Datenbank

**Lösung**:
1. Prüfen ob Container laufen: `docker ps | grep event-management-db`
2. Falls nicht: Run Config "Start Databases" ausführen
3. Anwendung neu starten

### "Port 5432 already in use"

**Problem**: Ein anderer Dienst verwendet bereits den Port

**Lösung**:
```bash
# Prüfen was auf Port läuft
lsof -i :5432

# Falls lokaler PostgreSQL läuft, stoppen
sudo service postgresql stop

# Falls anderer Container läuft
docker ps
docker stop <container-id>
```

### "Docker is not available"

**Problem**: Docker Desktop läuft nicht

**Lösung**:
1. Docker Desktop starten
2. Warten bis Docker vollständig gestartet ist
3. Testen mit: `docker ps`
4. Dann Container und App starten

### Container laufen, aber Verbindung schlägt fehl

**Lösung**:
```bash
# Container Logs prüfen
docker logs event-management-db-container

# Container neu starten
docker restart event-management-db-container

# Auf Gesundheit warten (ca. 5-10 Sekunden)
# Dann App neu starten
```

## Datenbank-Zugriff

Falls Sie direkt auf die Datenbank zugreifen möchten:

### Development Database

```bash
# Via psql
docker exec -it event-management-db-container psql -U postgres -d eventmanagement

# Oder in IDEA: Database Tool
# Host: localhost
# Port: 5432
# Database: eventmanagement
# User: postgres
# Password: postgres
```

### Production Database

```bash
# Via psql
docker exec -it event-management-db-prod-container psql -U postgres -d eventmanagement_prod

# Oder in IDEA: Database Tool
# Host: localhost
# Port: 5433
# Database: eventmanagement_prod
# User: postgres
# Password: postgres
```

## Zusammenfassung der Run Configurations

| Run Configuration | Zweck | Wann verwenden |
|------------------|-------|----------------|
| **Start Databases** | Startet DB-Container | Einmalig oder nach Computer-Neustart |
| **Development Mode** | Startet App im Dev-Modus | Tägliche Entwicklung |
| **Production Mode (Local)** | Startet App im Prod-Modus | Prod-Settings testen |
| **Production Mode (Docker)** | Vollständiger Docker-Stack | GraalVM Native Image testen |
| **Stop Databases** | Stoppt DB-Container | Aufräumen (optional) |
| **Stop Production Mode** | Stoppt Docker-Prod-Stack | Nach Prod-Docker-Test |
| **Build Production JAR** | Baut JAR-Datei | Deployment vorbereiten |

## Weiterführende Dokumentation

- **Detaillierte IDEA-Dokumentation**: [IDEA_SETUP.md](IDEA_SETUP.md)
- **Docker-Dokumentation**: [DOCKER.md](DOCKER.md)
- **Haupt-README**: [README.md](README.md)
- **Run Config Details**: [.idea/runConfigurations/README.md](.idea/runConfigurations/README.md)

## Checkliste: Erste Schritte

Folgen Sie dieser Checkliste für den ersten Start:

- [ ] Docker Desktop läuft
- [ ] Projekt in IDEA geöffnet
- [ ] Maven-Import abgeschlossen
- [ ] Java 21 in IDEA konfiguriert
- [ ] Run Config "Start Databases" ausgeführt
- [ ] Container laufen (geprüft mit `docker ps`)
- [ ] Run Config "Development Mode" ausgeführt
- [ ] Browser öffnet http://localhost:8080
- [ ] App läuft und zeigt Login-Seite

**Herzlichen Glückwunsch!** Sie haben das Event Management System erfolgreich gestartet! 🎉

## Quick Reference

### Ein-Befehl-Start (nach Einrichtung)

Wenn Container bereits laufen:
1. IDEA öffnen
2. "Development Mode" auswählen
3. `Shift+F10` drücken
4. Fertig!

### Container-Status prüfen

```bash
# Zeigt laufende Container
docker compose -f docker-compose.db.yml ps

# Zeigt Container-Logs
docker compose -f docker-compose.db.yml logs -f
```

### Alles zurücksetzen

```bash
# Container stoppen und Daten löschen
docker compose -f docker-compose.db.yml down -v

# Container neu starten
docker compose -f docker-compose.db.yml up -d

# In IDEA: Development Mode neu starten
```
