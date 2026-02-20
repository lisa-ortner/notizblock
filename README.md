# Notizblock Webanwendung

Eine vollständige Jakarta EE Webanwendung für die Verwaltung von Notizen mit JSF und PrimeFaces, inklusive Detailansicht
und automatischer Änderungshistorie.

## Tech-Stack

- **Jakarta EE 10** - Enterprise Java Platform
- **JSF (JavaServer Faces)** - MVC Framework für die UI
- **PrimeFaces 13** - UI-Komponentenbibliothek
- **JPA (Jakarta Persistence API)** - ORM für Datenbankzugriffe
- **CDI (Contexts and Dependency Injection)** - Dependency Injection
- **H2 Database** - In-Memory Datenbank
- **Hibernate** - JPA Implementation
- **Maven** - Build Management

## Projektstruktur

```
notizblock/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/gepardec/notizblock/
│   │   │       ├── entity/
│   │   │       │   ├── Note.java              # JPA Entity
│   │   │       │   ├── NoteHistory.java       # Historie-Entity
│   │   │       │   └── ChangeType.java        # Enum (CREATED, UPDATED, DELETED)
│   │   │       ├── repository/
│   │   │       │   ├── NoteRepository.java    # Note CRUD + Auto-History
│   │   │       │   └── NoteHistoryRepository.java  # Historie-Abfragen
│   │   │       └── bean/
│   │   │           ├── NoteBean.java          # JSF Backing Bean (Übersicht)
│   │   │           └── NoteDetailBean.java    # JSF Backing Bean (Detail)
│   │   ├── resources/
│   │   │   └── META-INF/
│   │   │       └── persistence.xml            # JPA Konfiguration
│   │   └── webapp/
│   │       ├── WEB-INF/
│   │       │   ├── web.xml                    # Web Application Config
│   │       │   ├── beans.xml                  # CDI Config
│   │       │   └── notizblock-ds.xml          # DataSource Config
│   │       ├── resources/
│   │       │   └── components/
│   │       │       ├── layout/
│   │       │       │   └── template.xhtml     # Facelets Template
│   │       │       ├── noteTable.xhtml        # DataTable Komponente
│   │       │       ├── noteFormDialog.xhtml   # Erstellen/Bearbeiten Dialog
│   │       │       └── deleteConfirmDialog.xhtml  # Lösch-Bestätigung
│   │       ├── index.xhtml                    # Übersichtsseite
│   │       └── detail.xhtml                   # Detailseite mit Historie
└── pom.xml                                    # Maven Dependencies
```

## Features

### 1. Notizen-Verwaltung (CRUD)

#### Übersichtsseite (index.xhtml)

- **Alle Notizen anzeigen** in interaktiver PrimeFaces DataTable
    - Spalten: Titel, Inhalt (gekürzt), Erstellungsdatum
    - Pagination (5, 10, 20 Einträge pro Seite)
    - Sortierung und Filterung nach Titel

- **Notiz erstellen**
    - Button "Neue Notiz" öffnet Dialog
    - Validierung für Titel und Inhalt
    - AJAX-Update der Liste nach dem Speichern

- **Notiz bearbeiten**
    - Stift-Icon öffnet Dialog mit vorausgefüllten Daten
    - AJAX-Update nach dem Speichern

- **Notiz löschen**
    - Mülleimer-Icon zeigt Bestätigungsdialog
    - AJAX-Update nach dem Löschen

- **Detail anzeigen**
    - Lupen-Icon navigiert zur Detailseite
    - Übergabe der Notiz-ID als Query-Parameter

### 2. Detailseite (detail.xhtml)

Die Detailseite zeigt vollständige Informationen zu einer Notiz:

- **Titel** - Vollständiger Titel
- **Inhalt** - Kompletter Inhalt (ohne Kürzung)
- **Erstellungsdatum** - Wann die Notiz erstellt wurde
- **Letztes Änderungsdatum** - Zeitpunkt der letzten Bearbeitung
- **Navigation** - "Zurück zur Liste" Button

### 3. Änderungshistorie

**Automatisches Tracking:**

- Jede CRUD-Operation (Create, Update, Delete) wird automatisch in der `NoteHistory`-Tabelle protokolliert
- Implementiert als transparenter Service im `NoteRepository`

**Timeline-Darstellung:**

- Vertikale Timeline mit allen Änderungen (älteste zuerst)
- Farbcodierte Icons:
    - 🟢 Grün: Notiz erstellt
    - 🟠 Orange: Notiz bearbeitet
- Zeitstempel für jede Änderung
- CSS-basierte Timeline (kein externes Plugin erforderlich)

### 4. Komponentenbasierte Architektur

**Facelets Template** (`layout/template.xhtml`):

- Wiederverwendbares Layout mit Header, Content-Bereich und Footer
- Konsistentes Design über alle Seiten
- Responsive Navigation

**Wiederverwendbare UI-Komponenten**:

- `noteTable.xhtml` - DataTable mit allen Action-Buttons
- `noteFormDialog.xhtml` - Erstellen/Bearbeiten Dialog
- `deleteConfirmDialog.xhtml` - Lösch-Bestätigung

**Vorteile**:

- DRY-Prinzip (Don't Repeat Yourself)
- Einfachere Wartung
- Konsistente UI

## Installation & Deployment

### Voraussetzungen

- **Java 17** oder höher
- **Maven 3.8+**
- **WildFly 27+** oder ein anderer Jakarta EE 10 kompatiblen Application Server

### Build

```bash
# Projekt klonen oder entpacken
cd notizblock

# Maven Build
mvn clean package
```

Das erstellt eine `notizblock.war` Datei im `target/` Verzeichnis.

### Deployment auf WildFly

#### Variante 1: Automatisches Deployment via Maven Plugin

```bash
# WildFly muss bereits laufen
mvn wildfly:deploy
```

#### Variante 2: Manuelles Deployment

1. WildFly starten:

```bash
cd $WILDFLY_HOME/bin
./standalone.sh  # (Linux/Mac)
standalone.bat   # (Windows)
```

2. WAR-Datei deployen:

```bash
cp target/notizblock.war $WILDFLY_HOME/standalone/deployments/
```

#### Variante 3: Web Console

1. WildFly Admin Console öffnen: `http://localhost:9990`
2. Deployments → Add → Upload `notizblock.war`

### H2 Datenbank Konfiguration

Die H2 In-Memory Datenbank wird automatisch konfiguriert. Falls WildFly das H2-Modul nicht enthält:

1. H2 JAR hinzufügen:

```bash
# H2 Driver ins WildFly modules Verzeichnis kopieren
mkdir -p $WILDFLY_HOME/modules/com/h2database/h2/main
cp ~/.m2/repository/com/h2database/h2/2.2.224/h2-2.2.224.jar \
   $WILDFLY_HOME/modules/com/h2database/h2/main/
```

2. `module.xml` erstellen in `$WILDFLY_HOME/modules/com/h2database/h2/main/`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<module xmlns="urn:jboss:module:1.9" name="com.h2database.h2">
    <resources>
        <resource-root path="h2-2.2.224.jar"/>
    </resources>
    <dependencies>
        <module name="javax.api"/>
        <module name="javax.transaction.api"/>
    </dependencies>
</module>
```

3. Driver in `standalone.xml` registrieren (im `<drivers>` Abschnitt):

```xml

<driver name="h2" module="com.h2database.h2">
    <xa-datasource-class>org.h2.jdbcx.JdbcDataSource</xa-datasource-class>
</driver>
```

## Anwendung aufrufen

Nach erfolgreichem Deployment:

```
http://localhost:8080/notizblock/           # Übersichtsseite
http://localhost:8080/notizblock/detail?id=1   # Detailseite (Beispiel mit ID 1)
```

## Entwicklermodus

Für die Entwicklung ist in `web.xml` der Project Stage auf `Development` gesetzt:

```xml

<context-param>
    <param-name>jakarta.faces.PROJECT_STAGE</param-name>
    <param-value>Development</param-value>
</context-param>
```

Dies aktiviert:

- Detaillierte Fehlermeldungen
- Keine Caching von Facelets
- Zusätzliche Debugging-Informationen

Für Produktion auf `Production` ändern.

## Datenmodell

### Note Entity

| Feld        | Typ           | Beschreibung                                        |
|-------------|---------------|-----------------------------------------------------|
| `id`        | Long          | Primary Key, auto-generiert                         |
| `title`     | String        | Titel der Notiz (max. 255 Zeichen)                  |
| `content`   | String        | Inhalt der Notiz (max. 5000 Zeichen)                |
| `createdAt` | LocalDateTime | Erstellungsdatum (automatisch via @PrePersist)      |
| `updatedAt` | LocalDateTime | Letztes Änderungsdatum (automatisch via @PreUpdate) |

### NoteHistory Entity

| Feld         | Typ           | Beschreibung                         |
|--------------|---------------|--------------------------------------|
| `id`         | Long          | Primary Key, auto-generiert          |
| `note`       | Note          | ManyToOne-Relation zur Notiz         |
| `changeType` | ChangeType    | Enum: CREATED, UPDATED, DELETED      |
| `changedAt`  | LocalDateTime | Zeitpunkt der Änderung (automatisch) |

### ChangeType Enum

```java
CREATED   // Notiz wurde erstellt
        UPDATED   // Notiz wurde bearbeitet
```

Jeder Wert hat:

- `displayName` - Anzeigetext für die UI
- `icon` - PrimeIcons CSS-Klasse

## Technische Details

### Architektur-Patterns

**Repository Pattern:**

- Trennung von Business-Logik und Datenzugriff
- `NoteRepository` und `NoteHistoryRepository` kapseln alle DB-Operationen

**Backing Bean Pattern:**

- `NoteBean` (@ViewScoped) für Übersichtsseite
- `NoteDetailBean` (@ViewScoped) für Detailseite mit ViewParam-Support

**Component-Based UI:**

- Facelets Template für konsistentes Layout
- `<ui:composition>` und `<ui:include>` für Komponenten-Wiederverwendung

### Automatisches History-Tracking

Implementierung in `NoteRepository`:

```java

@Transactional
public Note create(Note note) {
    entityManager.persist(note);
    entityManager.flush();
    historyRepository.createHistoryEntry(note, ChangeType.CREATED);
    return note;
}
```

Bei jeder Operation (`create`, `update`, `delete`) wird automatisch ein History-Eintrag erstellt.

### ViewParameter-Verarbeitung

Die Detailseite nutzt JSF ViewParams für die ID-Übergabe:

```xml

<f:metadata>
    <f:viewParam name="id" value="#{noteDetailBean.id}" required="true"/>
    <f:viewAction action="#{noteDetailBean.init}"/>
</f:metadata>
```

**Wichtig**: Die `init()`-Methode wird über `<f:viewAction>` aufgerufen (nicht `@PostConstruct`), damit der
ViewParameter bereits gebunden ist.

### Validierung

- **Jakarta Bean Validation** Annotations in der Entity (@NotBlank, etc.)
- **JSF Required-Validierung** in der UI (required="true")
- **Client-Side Validation** durch PrimeFaces
- **Server-Side Validation** vor dem Persistieren

### Transaktionsverwaltung

- JTA-Transaktionen via `@Transactional` im Repository
- Container-Managed Transactions
- Automatisches Rollback bei Exceptions

### CDI Scopes

- `@ApplicationScoped` - Repositories (Singleton)
- `@ViewScoped` - Backing Beans (Pro View-Instanz)
- Automatische Dependency Injection via `@Inject`

## UI/UX Features

### PrimeFaces Komponenten

- `p:dataTable` - Datentabelle mit Pagination, Sortierung, Filterung
- `p:dialog` - Modale Dialoge für CRUD-Operationen
- `p:growl` - Toast-Benachrichtigungen
- `p:card` - Karten-Layout für Timeline
- `p:button` - Navigation ohne AJAX
- `p:commandButton` - AJAX-fähige Buttons

### Responsive Design

- Viewport Meta-Tag für mobile Geräte
- PrimeFaces responsive grid system
- CSS-basierte Timeline passt sich an

### AJAX-Updates

Alle Operationen nutzen AJAX für bessere UX:

- `update=":mainForm:notesTable"` - Aktualisiert nur die Tabelle
- `process="@this"` - Verarbeitet nur den Button
- `oncomplete` - Callback nach erfolgreichem Update

## Troubleshooting

### ClassNotFoundException für PrimeFaces

Stelle sicher, dass PrimeFaces mit dem `jakarta` Classifier geladen wird:

```xml

<classifier>jakarta</classifier>
```

### DataSource nicht gefunden

Prüfe ob die JNDI-Namen in `persistence.xml` und `notizblock-ds.xml` übereinstimmen:

```
java:jboss/datasources/NotizblockDS
```

### Hibernate DDL-Fehler

Die `persistence.xml` nutzt `create-drop` für Development. Die Datenbank wird bei jedem Neustart neu erstellt.

### "Keine Notiz-ID angegeben" beim Öffnen der Detailseite

- Stelle sicher, dass die URL den `id`-Parameter enthält: `detail.xhtml?id=1`
- Prüfe, dass `<f:viewAction>` in der detail.xhtml vorhanden ist
- Die `init()`-Methode darf NICHT `@PostConstruct` haben

### Timeline wird nicht angezeigt

- CSS-Styles müssen im `<ui:define name="head">` Block sein
- Browser-Cache leeren nach CSS-Änderungen

## Best Practices

1. **Transaktionen**: Alle DB-Operationen in `@Transactional`-Methoden
2. **Error Handling**: Try-Catch in Bean-Methoden mit FacesMessage
3. **Lazy Loading**: `@ManyToOne(fetch = FetchType.LAZY)` für bessere Performance
4. **Validation**: Validierung auf Entity- UND UI-Ebene
5. **Komponenten**: Wiederverwendbare XHTML-Komponenten für DRY
6. **Separation of Concerns**: Repository → Service-Logik, Bean → UI-Logik

## Erweiterungsmöglichkeiten

- **Benutzer-Authentifizierung**: Login/Logout mit Jakarta Security
- **Tags/Kategorien**: Notizen kategorisieren und filtern
- **Volltextsuche**: Suche im Inhalt aller Notizen
- **Export/Import**: JSON oder XML Export
- **Anhänge**: Dateien an Notizen anhängen
- **Rich-Text Editor**: CKEditor oder TinyMCE Integration
- **REST API**: JAX-RS Endpoints für externe Clients
- **Persistente DB**: PostgreSQL oder MySQL statt H2

## Lizenz

Dieses Projekt ist ein Beispielprojekt für Lernzwecke.
