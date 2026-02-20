package com.gepardec.notizblock.repository;

import com.gepardec.notizblock.entity.Note;
import com.gepardec.notizblock.entity.NoteHistory;
import com.gepardec.notizblock.entity.ChangeType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;

/**
 * Repository für Datenbankoperationen auf NoteHistory-Entities
 */
@ApplicationScoped
public class NoteHistoryRepository {

    @PersistenceContext(unitName = "NotizblockPU")
    private EntityManager entityManager;

    /**
     * Erstellt einen neuen History-Eintrag
     * @param history Der zu speichernde History-Eintrag
     * @return Der gespeicherte History-Eintrag mit generierter ID
     */
    @Transactional
    public NoteHistory create(NoteHistory history) {
        entityManager.persist(history);
        entityManager.flush();
        return history;
    }

    /**
     * Erstellt einen neuen History-Eintrag für eine Notiz
     * @param note Die Notiz
     * @param changeType Der Typ der Änderung
     * @return Der gespeicherte History-Eintrag
     */
    @Transactional
    public NoteHistory createHistoryEntry(Note note, ChangeType changeType) {
        NoteHistory history = new NoteHistory(note, changeType);
        return create(history);
    }

    /**
     * Findet alle History-Einträge für eine bestimmte Notiz, sortiert nach Zeitstempel (älteste zuerst)
     * @param noteId Die ID der Notiz
     * @return Liste aller History-Einträge für diese Notiz
     */
    public List<NoteHistory> findByNoteId(Long noteId) {
        return entityManager.createQuery(
                "SELECT h FROM NoteHistory h WHERE h.note.id = :noteId ORDER BY h.changedAt ASC",
                NoteHistory.class)
                .setParameter("noteId", noteId)
                .getResultList();
    }

    /**
     * Findet alle History-Einträge für eine bestimmte Notiz, sortiert nach Zeitstempel (älteste zuerst)
     * @param note Die Notiz
     * @return Liste aller History-Einträge für diese Notiz
     */
    public List<NoteHistory> findByNote(Note note) {
        if (note == null || note.getId() == null) {
            return List.of();
        }
        return findByNoteId(note.getId());
    }

    /**
     * Zählt die Anzahl der History-Einträge für eine Notiz
     * @param noteId Die ID der Notiz
     * @return Anzahl der History-Einträge
     */
    public Long countByNoteId(Long noteId) {
        return entityManager.createQuery(
                "SELECT COUNT(h) FROM NoteHistory h WHERE h.note.id = :noteId",
                Long.class)
                .setParameter("noteId", noteId)
                .getSingleResult();
    }

    /**
     * Löscht alle History-Einträge für eine bestimmte Notiz
     * @param noteId Die ID der Notiz
     * @return Anzahl der gelöschten Einträge
     */
    @Transactional
    public int deleteByNoteId(Long noteId) {
        return entityManager.createQuery(
                "DELETE FROM NoteHistory h WHERE h.note.id = :noteId")
                .setParameter("noteId", noteId)
                .executeUpdate();
    }
}
