package de.doday.backend.settings;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/** Eingabe beim Ändern der Einstellungen. */
public record SettingsRequest(
        @NotNull(message = "Die Anzahl der Gewohnheiten fehlt.")
        @Min(value = 1, message = "Mindestens eine Gewohnheit muss für die Flamme reichen.")
        Integer requiredHabits) {}
