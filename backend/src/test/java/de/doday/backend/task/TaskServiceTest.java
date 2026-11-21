package de.doday.backend.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import de.doday.backend.error.NotFoundException;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class TaskServiceTest {

    @Autowired
    private TaskService service;

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
        int before = service.findAll().size();
        TaskRequest request = new TaskRequest("  Test  ", LocalDate.of(2026, 11, 20), Priority.NORMAL, null);

        Task created = service.create(request);

        show("ID der neuen Aufgabe", "nicht null", created.getId());
        assertThat(created.getId()).as("ID der neuen Aufgabe").isNotNull();

        show("Titel (Leerzeichen entfernt)", "Test", created.getTitle());
        assertThat(created.getTitle()).as("Titel (Leerzeichen entfernt)").isEqualTo("Test");

        show("done ohne Angabe", false, created.isDone());
        assertThat(created.isDone()).as("done ohne Angabe").isFalse();

        show("Anzahl Aufgaben danach", before + 1, service.findAll().size());
        assertThat(service.findAll()).as("Anzahl Aufgaben danach").hasSize(before + 1);
    }

    @Test
    @DisplayName("Aufgabe ändern: Felder ändern sich, done bleibt erhalten")
    void updateChangesFieldsAndKeepsDoneWhenMissing() {
        // Die zweite Beispielaufgabe ("Wäsche waschen") ist schon erledigt
        Task second = service.findAll().get(1);
        TaskRequest request = new TaskRequest("Neu", LocalDate.of(2026, 12, 1), Priority.OPTIONAL, null);

        Task updated = service.update(second.getId(), request);

        show("Titel", "Neu", updated.getTitle());
        assertThat(updated.getTitle()).as("Titel").isEqualTo("Neu");

        show("Priorität", Priority.OPTIONAL, updated.getPriority());
        assertThat(updated.getPriority()).as("Priorität").isEqualTo(Priority.OPTIONAL);

        // Die Aufgabe war schon erledigt und bleibt es, weil done im Request fehlt
        show("done bleibt erhalten", true, updated.isDone());
        assertThat(updated.isDone()).as("done bleibt erhalten").isTrue();
    }

    @Test
    @DisplayName("Aufgabe als erledigt markieren")
    void updateCanMarkTaskAsDone() {
        Task first = service.findAll().get(0);
        TaskRequest request = new TaskRequest("Konzept", LocalDate.of(2026, 11, 20), Priority.MUST_DO, true);

        Task updated = service.update(first.getId(), request);

        show("done nach Update", true, updated.isDone());
        assertThat(updated.isDone()).as("done nach Update").isTrue();
    }

    @Test
    @DisplayName("Aufgabe löschen")
    void deleteRemovesTask() {
        int before = service.findAll().size();
        Long id = service.findAll().get(0).getId();

        service.delete(id);

        int actual = service.findAll().size();
        show("Anzahl Aufgaben nach dem Löschen", before - 1, actual);
        assertThat(actual).as("Anzahl Aufgaben nach dem Löschen").isEqualTo(before - 1);

        assertThatThrownBy(() -> service.findById(id))
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