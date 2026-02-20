package com.gepardec.notizblock.entity;

/**
 * Enum für verschiedene Typen von Änderungen an einer Notiz
 */
public enum ChangeType {
    CREATED("Erstellt", "pi pi-plus-circle"),
    UPDATED("Bearbeitet", "pi pi-pencil"),
    DELETED("Gelöscht", "pi pi-trash");

    private final String displayName;
    private final String icon;

    ChangeType(String displayName, String icon) {
        this.displayName = displayName;
        this.icon = icon;
    }

    /**
     * @return Anzeigetext für den ChangeType
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * @return PrimeIcons CSS-Klasse für den ChangeType
     */
    public String getIcon() {
        return icon;
    }
}
