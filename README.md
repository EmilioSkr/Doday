# Doday Backend

REST-API für **Doday**: Aufgaben planen und Gewohnheiten aufbauen. Gebaut mit Spring Boot 4, Java 25 und Gradle.

Stand: Die Daten liegen im Arbeitsspeicher und sind nach einem Neustart wieder die Beispieldaten. Die Datenbank (PostgreSQL) folgt in einem späteren Meilenstein.

## Voraussetzungen

- Java 25 (JDK)
- Keine eigene Gradle-Installation nötig, der Gradle Wrapper (`gradlew`) ist dabei

## Starten

Im Ordner `backend`:

```powershell
.\gradlew.bat bootRun
```

Unter Linux und macOS: `./gradlew bootRun`.

Die API läuft dann unter `http://localhost:8080`. Beenden mit `Strg + C`.

## Prüfen, ob es läuft

Im Browser `http://localhost:8080/api/tasks` öffnen. Es erscheint eine JSON-Liste mit sechs Beispielaufgaben.

Oder in PowerShell:

```powershell
Invoke-RestMethod http://localhost:8080/api/tasks
```

Aufgabe anlegen:

```powershell
Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/tasks `
  -ContentType 'application/json' `
  -Body '{"title":"Test","dueDate":"2026-11-20","priority":"NORMAL"}'
```

Aufgabe löschen:

```powershell
Invoke-RestMethod -Method Delete -Uri http://localhost:8080/api/tasks/1
```

## Tests

```powershell
.\gradlew.bat test
```

Ein ausführlicher HTML-Bericht liegt danach unter `build/reports/tests/test/index.html`.

## API

| Methode | Adresse               | Wirkung                          | Antwort |
| ------- | --------------------- | -------------------------------- | ------- |
| GET     | `/api/tasks`          | alle Aufgaben                    | 200     |
| GET     | `/api/tasks/{id}`     | eine Aufgabe                     | 200/404 |
| POST    | `/api/tasks`          | Aufgabe anlegen                  | 201/400 |
| PUT     | `/api/tasks/{id}`     | Aufgabe ändern (auch `done`)     | 200/400/404 |
| DELETE  | `/api/tasks/{id}`     | Aufgabe löschen                  | 204/404 |
| GET     | `/api/habits`         | alle Gewohnheiten                | 200     |
| GET     | `/api/habits/{id}`    | eine Gewohnheit                  | 200/404 |
| POST    | `/api/habits`         | Gewohnheit anlegen               | 201/400 |
| PUT     | `/api/habits/{id}`    | Gewohnheit ändern (auch `done`)  | 200/400/404 |
| DELETE  | `/api/habits/{id}`    | Gewohnheit löschen               | 204/404 |

Beispiel einer Aufgabe:

```json
{ "id": 1, "title": "Konzept abgeben", "dueDate": "2026-11-20", "priority": "MUST_DO", "done": false }
```

- `priority`: `MUST_DO`, `NORMAL` oder `OPTIONAL`
- `dueDate`: Datum als `YYYY-MM-DD`
- Beim Anlegen und Ändern ist `done` optional. Fehlt es, bleibt es `false` bzw. unverändert.

Fehler kommen als JSON, bei ungültiger Eingabe mit einer Meldung pro Feld:

```json
{ "status": 400, "detail": "Die Eingabe ist ungültig.", "errors": { "title": "Der Titel darf nicht leer sein." } }
```

## Konfiguration

Einstellungen in `src/main/resources/application.properties`, per Umgebungsvariable überschreibbar:

| Variable               | Bedeutung                                       | Standard                |
| ---------------------- | ----------------------------------------------- | ----------------------- |
| `PORT`                 | Port des Servers (Render setzt ihn selbst)      | `8080`                  |
| `CORS_ALLOWED_ORIGINS` | Adressen des Frontends, mehrere mit Komma       | `http://localhost:5173` |

Zugangsdaten gehören nicht in dieses Repository.

## Projektstruktur

```
src/main/java/de/doday/backend/
  task/      Aufgaben (Controller, Service, Request, Entity)
  habit/     Gewohnheiten
  error/     Fehlerbehandlung (404 und 400 als JSON)
  config/    CORS für das Vue-Frontend
src/test/    Tests der Services
```

## Häufige Probleme

- **`Directory ... does not contain a Gradle build`:** Du bist im falschen Ordner. Wechsle mit `cd backend` dorthin, wo `gradlew.bat` liegt.
- **Port 8080 ist belegt:** Beende den anderen Prozess oder starte mit `$env:PORT=8081; .\gradlew.bat bootRun`.
- **Das Frontend bekommt einen CORS-Fehler:** Prüfe `CORS_ALLOWED_ORIGINS`. Die Adresse muss genau der des Frontends entsprechen, also mit Port und ohne Schrägstrich am Ende.
