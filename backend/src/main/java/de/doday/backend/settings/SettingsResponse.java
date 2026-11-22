package de.doday.backend.settings;

/**
 * Ausgabe der Einstellungen.
 *
 * @param requiredHabits wirksame Anzahl für die Flamme, immer zwischen 1 und {@code totalHabits}
 * @param totalHabits    Anzahl der aktuell vorhandenen Gewohnheiten (Obergrenze des Reglers)
 */
public record SettingsResponse(int requiredHabits, int totalHabits) {}
