package com.gepardec.notizblock.bean;

import com.gepardec.notizblock.entity.Note;
import com.gepardec.notizblock.entity.NoteHistory;
import com.gepardec.notizblock.repository.NoteHistoryRepository;
import com.gepardec.notizblock.repository.NoteRepository;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * ViewScoped Backing Bean für die Detail-Ansicht einer Notiz
 * Zeigt alle Details der Notiz sowie die vollständige Änderungshistorie an
 */
@Named
@ViewScoped
public class NoteDetailBean implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Inject
    private NoteRepository noteRepository;

    @Inject
    private NoteHistoryRepository historyRepository;

    // ID der anzuzeigenden Notiz (aus Query-Parameter)
    private Long id;

    // Die aktuell angezeigte Notiz
    private Note note;

    // Historie-Einträge für diese Notiz
    private List<NoteHistory> history;

    /**
     * Initialisierung - wird über f:viewAction aufgerufen
     * Lädt die Notiz und ihre Historie basierend auf dem Query-Parameter 'id'
     */
    public void init() {
        if (id != null) {
            loadNote();
            loadHistory();
        } else {
            // Falls keine ID übergeben wurde, Fehler anzeigen
            addMessage(FacesMessage.SEVERITY_ERROR, "Fehler",
                    "Keine Notiz-ID angegeben");
        }
    }

    /**
     * Lädt die Notiz anhand der ID
     */
    private void loadNote() {
        noteRepository.findById(id).ifPresentOrElse(
                foundNote -> this.note = foundNote,
                () -> addMessage(FacesMessage.SEVERITY_ERROR, "Fehler",
                        "Notiz mit ID " + id + " wurde nicht gefunden")
        );
    }

    /**
     * Lädt die Historie-Einträge für die aktuelle Notiz
     */
    private void loadHistory() {
        if (note != null) {
            history = historyRepository.findByNote(note);
        }
    }

    /**
     * Navigation zurück zur Übersicht
     *
     * @return Navigation Outcome
     */
    public String backToList() {
        return "index?faces-redirect=true";
    }

    /**
     * Navigation zum Bearbeiten der Notiz
     *
     * @return Navigation Outcome
     */
    public String editNote() {
        // Hier könnte man direkt zur index-Seite mit einem Parameter navigieren
        // oder einen Edit-Dialog auf der Detail-Seite öffnen
        return "index?faces-redirect=true";
    }

    /**
     * Hilfsmethode zum Hinzufügen von Faces-Nachrichten
     */
    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(severity, summary, detail));
    }

    // Getter und Setter

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Note getNote() {
        return note;
    }

    public void setNote(Note note) {
        this.note = note;
    }

    public List<NoteHistory> getHistory() {
        return history;
    }

    public void setHistory(List<NoteHistory> history) {
        this.history = history;
    }

    /**
     * Prüft ob eine Notiz geladen wurde
     *
     * @return true wenn Notiz vorhanden
     */
    public boolean hasNote() {
        return note != null;
    }

    /**
     * Prüft ob Historie vorhanden ist
     *
     * @return true wenn Historie-Einträge vorhanden sind
     */
    public boolean hasHistory() {
        return history != null && !history.isEmpty();
    }

    /**
     * Gibt die Anzahl der Historie-Einträge zurück
     *
     * @return Anzahl der Einträge
     */
    public int getHistoryCount() {
        return history != null ? history.size() : 0;
    }
}
