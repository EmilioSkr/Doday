package de.doday.backend.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import de.doday.backend.error.NotFoundException;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

class TaskServiceTest {

    private final TaskService service = new TaskService();

    @BeforeEach
    void printTestName(TestInfo info) {
        System.out.println("\n=== " + info.getDisplayName() + " ===");
    }

    /** Gibt erwartet und erhalten aus, auch wenn der Test besteht. */
    private static void show(String check, Object expected, Object actual) {
        System.out.println(check + "\n    erwartet: " + expected + "\n    erhalten: " + actual);
    }

    @Test
    @DisplayName("Startet mit sechs Beispielaufgaben")
    void startsWithSampleTasks() {
        int expected = 6;

        int actual = service.findAll().size();

        show("Anzahl Beispielaufgaben", expected, actual);
        assertThat(actual).as("Anzahl Beispielaufgaben").isEqualTo(expected);
    }

    @Test
    @DisplayName("Aufgabe erstellen: neue ID, Titel ohne Leerzeichen, nicht erledigt")
    void createAddsTaskWithNewIdAndNotDone() {
        TaskRequest request = new TaskRequest("  Test  ", LocalDate.of(2026, 11, 20), Priority.NORMAL, null);

        Task created = service.create(request);

        show("ID der neuen Aufgabe", 7L, created.getId());
        assertThat(created.getId()).as("ID der neuen Aufgabe").isEqualTo(7L);

        show("Titel (Leerzeichen entfernt)", "Test", created.getTitle());
        assertThat(created.getTitle()).as("Titel (Leerzeichen entfernt)").isEqualTo("Test");

        show("done ohne Angabe", false, created.isDone());
        assertThat(created.isDone()).as("done ohne Angabe").isFalse();

        show("Anzahl Aufgaben danach", 7, service.findAll().size());
        assertThat(service.findAll()).as("Anzahl Aufgaben danach").hasSize(7);
    }

    @Test
    @DisplayName("Aufgabe ändern: Felder ändern sich, done bleibt erhalten")
    void updateChangesFieldsAndKeepsDoneWhenMissing() {
        TaskRequest request = new TaskRequest("Neu", LocalDate.of(2026, 12, 1), Priority.OPTIONAL, null);

        Task updated = service.update(2L, request);

        show("Titel", "Neu", updated.getTitle());
        assertThat(updated.getTitle()).as("Titel").isEqualTo("Neu");

        show("Priorität", Priority.OPTIONAL, updated.getPriority());
        assertThat(updated.getPriority()).as("Priorität").isEqualTo(Priority.OPTIONAL);

        // Aufgabe 2 war schon erledigt und bleibt es, weil done im Request fehlt
        show("done bleibt erhalten", true, updated.isDone());
        assertThat(updated.isDone()).as("done bleibt erhalten").isTrue();
    }

    @Test
    @DisplayName("Aufgabe als erledigt markieren")
    void updateCanMarkTaskAsDone() {
        TaskRequest request = new TaskRequest("Konzept", LocalDate.of(2026, 11, 20), Priority.MUST_DO, true);

        Task updated = service.update(1L, request);

        show("done nach Update", true, updated.isDone());
        assertThat(updated.isDone()).as("done nach Update").isTrue();
    }

    @Test
    @DisplayName("Aufgabe löschen")
    void deleteRemovesTask() {
        service.delete(1L);

        int actual = service.findAll().size();
        show("Anzahl Aufgaben nach dem Löschen", 5, actual);
        assertThat(actual).as("Anzahl Aufgaben nach dem Löschen").isEqualTo(5);

        assertThatThrownBy(() -> service.findById(1L))
                .as("Gelöschte Aufgabe wird nicht mehr gefunden")
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("Unbekannte ID gibt NotFound")
    void unknownIdThrowsNotFound() {
        assertThatThrownBy(() -> service.findById(99L))
                .as("Unbekannte ID 99")
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("99");
    }
}