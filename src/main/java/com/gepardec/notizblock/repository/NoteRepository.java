package com.gepardec.notizblock.repository;

import com.gepardec.notizblock.entity.Note;
import com.gepardec.notizblock.entity.ChangeType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * Repository für Datenbankoperationen auf Note-Entities
 * Nutzt CDI und JPA für Dependency Injection und Persistierung
 */
@ApplicationScoped
public class NoteRepository {

    @PersistenceContext(unitName = "NotizblockPU")
    private EntityManager entityManager;

    @Inject
    private NoteHistoryRepository historyRepository;

    /**
     * Gibt alle Notizen zurück, sortiert nach Erstellungsdatum (neueste zuerst)
     * @return Liste aller Notizen
     */
    public List<Note> findAll() {
        return entityManager.createQuery(
                "SELECT n FROM Note n ORDER BY n.createdAt DESC", Note.class)
                .getResultList();
    }

    /**
     * Findet eine Notiz anhand ihrer ID
     * @param id Die ID der Notiz
     * @return Optional mit der gefundenen Notiz oder leer
     */
    public Optional<Note> findById(Long id) {
        Note note = entityManager.find(Note.class, id);
        return Optional.ofNullable(note);
    }

    /**
     * Erstellt eine neue Notiz in der Datenbank
     * @param note Die zu speichernde Notiz
     * @return Die gespeicherte Notiz mit generierter ID
     */
    @Transactional
    public Note create(Note note) {
        entityManager.persist(note);
        entityManager.flush();
        // History-Eintrag für Erstellung
        historyRepository.createHistoryEntry(note, ChangeType.CREATED);
        return note;
    }

    /**
     * Aktualisiert eine bestehende Notiz
     * @param note Die zu aktualisierende Notiz
     * @return Die aktualisierte Notiz
     */
    @Transactional
    public Note update(Note note) {
        Note merged = entityManager.merge(note);
        entityManager.flush();
        // History-Eintrag für Update
        historyRepository.createHistoryEntry(merged, ChangeType.UPDATED);
        return merged;
    }

    /**
     * Löscht eine Notiz anhand ihrer ID
     * @param id Die ID der zu löschenden Notiz
     */
    @Transactional
    public void delete(Long id) {
        findById(id).ifPresent(note -> {
            // History-Eintrag für Löschen BEVOR die Notiz gelöscht wird
            historyRepository.createHistoryEntry(note, ChangeType.DELETED);
            // Falls die Entity nicht managed ist, erst mergen
            if (!entityManager.contains(note)) {
                note = entityManager.merge(note);
            }
            entityManager.remove(note);
        });
    }

    /**
     * Löscht eine Notiz direkt
     * @param note Die zu löschende Notiz
     */
    @Transactional
    public void delete(Note note) {
        if (note != null && note.getId() != null) {
            delete(note.getId());
        }
    }

    /**
     * Zählt die Anzahl aller Notizen
     * @return Anzahl der Notizen
     */
    public Long count() {
        return entityManager.createQuery(
                "SELECT COUNT(n) FROM Note n", Long.class)
                .getSingleResult();
    }
}
