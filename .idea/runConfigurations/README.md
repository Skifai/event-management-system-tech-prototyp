# IntelliJ IDEA Run Configurations

Dieses Projekt enthält vorkonfigurierte Run Configurations für eine einfache Verwendung in IntelliJ IDEA.

## Übersicht

| Run Configuration | Typ | Zweck | Voraussetzungen |
|-------------------|-----|-------|-----------------|
| **Development Mode** | Spring Boot | Startet App im Development-Modus | PostgreSQL Container läuft |
| **Production Mode (Local)** | Spring Boot | Startet App im Production-Modus lokal | PostgreSQL Container läuft |
| **Production Mode (Docker)** | Docker Compose | Baut und startet vollständigen Production-Stack | Docker Desktop läuft |
| **Start Databases** | Docker Compose | Startet alle Datenbank-Container | Docker Desktop läuft |
| **Stop Databases** | Docker Compose | Stoppt alle Datenbank-Container | - |
| **Stop Production Mode** | Docker Compose | Stoppt Production-Stack | - |
| **Run Tests** | Maven | Führt alle Tests aus | Test-DB Container läuft |

## Detaillierte Beschreibung

### Development Mode
- **Profil**: `dev`
- **Port**: 8080
- **Datenbank**: PostgreSQL auf Port 5432
- **Features**: Hot-Reload (DevTools), SQL-Logging, Testdaten
- **Start-Zeit**: ~10 Sekunden

### Production Mode (Docker)
- **Profil**: `prod` (automatisch im Container)
- **Port**: 8080 (Host) → 8081 (Container)
- **Build-Zeit**: ~2-3 Minuten (beim ersten Mal)

### Start Databases
Startet alle PostgreSQL Datenbank-Container:
- Development DB: Port 5432
- Test DB: Port 5434
- Production DB: Port 5433

### Run Tests
Führt alle 49 Tests mit Maven aus. Benötigt Test-DB auf Port 5434.

## Typische Workflows

### Tägliche Entwicklung
1. Docker Desktop starten
2. "Start Databases" ausführen
3. "Development Mode" starten

### Production-Test
1. "Production Mode (Docker)" starten
2. Testen auf http://localhost:8080
3. "Stop Production Mode" ausführen

Weitere Details: [../../README.md](../../README.md)
