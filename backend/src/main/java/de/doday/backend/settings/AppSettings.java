package de.doday.backend.settings;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Einstellungen der App. Es gibt genau eine Zeile mit der ID {@link #SINGLETON_ID}. */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AppSettings {

    public static final long SINGLETON_ID = 1L;

    @Id
    private Long id;

    /** Wie viele Gewohnheiten an einem Tag erledigt sein müssen, damit die Flamme zählt (mindestens 1). */
    private int requiredHabits;

    /** Der Tag, für den die Gewohnheiten zuletzt zurückgesetzt wurden (siehe StreakService). */
    private LocalDate lastRolloverDate;
}
