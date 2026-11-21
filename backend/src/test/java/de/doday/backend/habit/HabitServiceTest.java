package de.doday.backend.habit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import de.doday.backend.error.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class HabitServiceTest {

    @Autowired
    private HabitService service;

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
        int before = service.findAll().size();

        Habit created = service.create(new HabitRequest(" Dehnen ", null));

        show("Name (Leerzeichen entfernt)", "Dehnen", created.getName());
        assertThat(created.getName()).as("Name (Leerzeichen entfernt)").isEqualTo("Dehnen");

        show("done ohne Angabe", false, created.isDone());
        assertThat(created.isDone()).as("done ohne Angabe").isFalse();

        show("Anzahl Gewohnheiten danach", before + 1, service.findAll().size());
        assertThat(service.findAll()).as("Anzahl Gewohnheiten danach").hasSize(before + 1);
    }

    @Test
    void updateTogglesDone() {
        // Die vierte Beispielgewohnheit ("Vokabeln lernen") ist noch nicht erledigt
        Habit fourth = service.findAll().get(3);

        Habit updated = service.update(fourth.getId(), new HabitRequest("Vokabeln lernen", true));

        show("done nach Update", true, updated.isDone());
        assertThat(updated.isDone()).as("done nach Update").isTrue();
    }

    @Test
    void deleteRemovesHabit() {
        Long id = service.findAll().get(0).getId();

        service.delete(id);

        assertThatThrownBy(() -> service.findById(id))
                .as("Gelöschte Gewohnheit wird nicht mehr gefunden")
                .isInstanceOf(NotFoundException.class);
    }
}