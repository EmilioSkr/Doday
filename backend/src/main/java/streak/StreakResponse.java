package de.doday.backend.streak;

import java.time.LocalDate;
import java.util.List;

/**
 * Streak-Daten für das Dashboard.
 *
 * @param currentStreak  Tage in Folge mit Flamme (heute zählt mit, sobald die Flamme für heute erreicht ist)
 * @param longestStreak  längste Serie überhaupt
 * @param flameToday     ob heute schon genug Gewohnheiten erledigt sind
 * @param doneToday      heute erledigte Gewohnheiten
 * @param totalHabits    aktuell vorhandene Gewohnheiten
 * @param requiredHabits wirksame Anzahl für die Flamme (zwischen 1 und {@code totalHabits})
 * @param week           die letzten sieben Tage, ältester zuerst, der letzte ist heute
 */
public record StreakResponse(
        int currentStreak,
        int longestStreak,
        boolean flameToday,
        int doneToday,
        int totalHabits,
        int requiredHabits,
        List<DayStatus> week) {

    public record DayStatus(LocalDate date, boolean flame) {}
}
