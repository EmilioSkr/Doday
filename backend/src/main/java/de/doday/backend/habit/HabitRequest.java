package de.doday.backend.habit;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Eingabe beim Erstellen und Ändern einer Gewohnheit. {@code done} ist optional. */
public record HabitRequest(
        @NotBlank(message = "Der Name darf nicht leer sein.")
        @Size(max = 100, message = "Der Name darf höchstens 100 Zeichen lang sein.")
        String name,
        Boolean done) {}