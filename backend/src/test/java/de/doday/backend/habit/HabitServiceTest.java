package de.doday.backend.habit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import de.doday.backend.error.NotFoundException;
import org.junit.jupiter.api.Test;

class HabitServiceTest {

    private final HabitService service = new HabitService();

    /** Gibt erwartet und erhalten aus, auch wenn der Test besteht. */
    private static void show(String check, Object expected, Object actual) {
        System.out.println(check + "\n    erwartet: " + expected + "\n    erhalten: " + actual);
    }

    @Test
    void startsWithSampleHabits() {
        int expected = 5;

        int actual = service.findAll().size();

        show("Anzahl Beispielgewohnheiten", expected, actual);
        assertThat(actual).as("Anzahl Beispielgewohnheiten").isEqualTo(expected);
    }

    @Test
    void createAddsHabitNotDone() {
        Habit created = service.create(new HabitRequest(" Dehnen ", null));

        show("Name (Leerzeichen entfernt)", "Dehnen", created.getName());
        assertThat(created.getName()).as("Name (Leerzeichen entfernt)").isEqualTo("Dehnen");

        show("done ohne Angabe", false, created.isDone());
        assertThat(created.isDone()).as("done ohne Angabe").isFalse();

        show("Anzahl Gewohnheiten danach", 6, service.findAll().size());
        assertThat(service.findAll()).as("Anzahl Gewohnheiten danach").hasSize(6);
    }

    @Test
    void updateTogglesDone() {
        Habit updated = service.update(4L, new HabitRequest("Vokabeln lernen", true));

        show("done nach Update", true, updated.isDone());
        assertThat(updated.isDone()).as("done nach Update").isTrue();
    }

    @Test
    void deleteRemovesHabit() {
        service.delete(1L);

        assertThatThrownBy(() -> service.findById(1L))
                .as("Gelöschte Gewohnheit wird nicht mehr gefunden")
                .isInstanceOf(NotFoundException.class);
    }
}