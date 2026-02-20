package com.gepardec.notizblock.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * JPA Entity für die Änderungshistorie einer Notiz
 */
@Entity
@Table(name = "note_history")
public class NoteHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "note_id", nullable = false)
    private Note note;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ChangeType changeType;

    @Column(nullable = false, updatable = false)
    private LocalDateTime changedAt;

    /**
     * Default Constructor
     */
    public NoteHistory() {
    }

    /**
     * Constructor mit allen Pflichtfeldern
     */
    public NoteHistory(Note note, ChangeType changeType) {
        this.note = note;
        this.changeType = changeType;
    }

    /**
     * PrePersist Callback - setzt changedAt automatisch vor dem ersten Speichern
     */
    @PrePersist
    protected void onCreate() {
        this.changedAt = LocalDateTime.now();
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

    public ChangeType getChangeType() {
        return changeType;
    }

    public void setChangeType(ChangeType changeType) {
        this.changeType = changeType;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(LocalDateTime changedAt) {
        this.changedAt = changedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NoteHistory that = (NoteHistory) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "NoteHistory{" +
                "id=" + id +
                ", changeType=" + changeType +
                ", changedAt=" + changedAt +
                '}';
    }
}
