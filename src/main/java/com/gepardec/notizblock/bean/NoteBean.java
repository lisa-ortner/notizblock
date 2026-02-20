package com.gepardec.notizblock.bean;

import com.gepardec.notizblock.entity.Note;
import com.gepardec.notizblock.repository.NoteRepository;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * ViewScoped Backing Bean für die Notizblock-Webseite
 * Verwaltet den Zustand und die Aktionen für die JSF-Seite
 */
@Named
@ViewScoped
public class NoteBean implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Inject
    private NoteRepository noteRepository;

    // Liste aller Notizen
    private List<Note> notes;

    // Aktuelle Notiz (für Erstellen/Bearbeiten)
    private Note currentNote;

    // Flag ob Dialog im Bearbeitungsmodus ist
    private boolean editMode;

    /**
     * Initialisierung nach Bean-Erstellung
     */
    @PostConstruct
    public void init() {
        loadNotes();
        prepareNewNote();
    }

    /**
     * Lädt alle Notizen aus der Datenbank
     */
    public void loadNotes() {
        notes = noteRepository.findAll();
    }

    /**
     * Bereitet eine neue leere Notiz vor (für den Create-Dialog)
     */
    public void prepareNewNote() {
        currentNote = new Note();
        editMode = false;
    }

    /**
     * Bereitet das Bearbeiten einer existierenden Notiz vor
     *
     * @param note Die zu bearbeitende Notiz
     */
    public void prepareEditNote(Note note) {
        // Erstelle eine Kopie, um die ursprüngliche Notiz nicht zu ändern
        this.currentNote = new Note();
        this.currentNote.setId(note.getId());
        this.currentNote.setTitle(note.getTitle());
        this.currentNote.setContent(note.getContent());
        this.currentNote.setCreatedAt(note.getCreatedAt());
        this.currentNote.setUpdatedAt(note.getUpdatedAt());
        this.editMode = true;
    }

    /**
     * Speichert die aktuelle Notiz (Create oder Update)
     */
    public void saveNote() {
        try {
            if (editMode) {
                // Update
                noteRepository.update(currentNote);
                addMessage(FacesMessage.SEVERITY_INFO, "Erfolg",
                        "Notiz wurde erfolgreich aktualisiert");
            } else {
                // Create
                noteRepository.create(currentNote);
                addMessage(FacesMessage.SEVERITY_INFO, "Erfolg",
                        "Notiz wurde erfolgreich erstellt");
            }

            // Liste neu laden und Dialog schließen
            loadNotes();
            prepareNewNote();

        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Fehler",
                    "Notiz konnte nicht gespeichert werden: " + e.getMessage());
        }
    }

    /**
     * Löscht eine Notiz
     *
     * @param note Die zu löschende Notiz
     */
    public void deleteNote(Note note) {
        try {
            noteRepository.delete(note);
            loadNotes();
            addMessage(FacesMessage.SEVERITY_INFO, "Erfolg",
                    "Notiz wurde erfolgreich gelöscht");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Fehler",
                    "Notiz konnte nicht gelöscht werden: " + e.getMessage());
        }
    }

    /**
     * Bricht die aktuelle Bearbeitung ab
     */
    public void cancelEdit() {
        prepareNewNote();
    }

    /**
     * Hilfsmethode zum Hinzufügen von Faces-Nachrichten
     */
    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance()
                .addMessage(null, new FacesMessage(severity, summary, detail));
    }

    // Getter und Setter

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }

    public Note getCurrentNote() {
        return currentNote;
    }

    public void setCurrentNote(Note currentNote) {
        this.currentNote = currentNote;
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    /**
     * Gibt die Anzahl der Notizen zurück
     */
    public int getNotesCount() {
        return notes != null ? notes.size() : 0;
    }
}
