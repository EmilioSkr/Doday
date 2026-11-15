package de.doday.backend.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/** Eingabe beim Erstellen und Ändern einer Aufgabe. {@code done} ist optional. */
public record TaskRequest(
        @NotBlank(message = "Der Titel darf nicht leer sein.")
        @Size(max = 200, message = "Der Titel darf höchstens 200 Zeichen lang sein.")
        String title,
        @NotNull(message = "Das Fälligkeitsdatum fehlt.") LocalDate dueDate,
        @NotNull(message = "Die Priorität fehlt.") Priority priority,
        Boolean done) {}