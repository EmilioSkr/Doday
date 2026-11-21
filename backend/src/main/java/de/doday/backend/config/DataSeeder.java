package de.doday.backend.config;

import de.doday.backend.habit.Habit;
import de.doday.backend.habit.HabitRepository;
import de.doday.backend.task.Priority;
import de.doday.backend.task.Task;
import de.doday.backend.task.TaskRepository;
import java.time.LocalDate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final TaskRepository tasks;
    private final HabitRepository habits;

    public DataSeeder(TaskRepository tasks, HabitRepository habits) {
        this.tasks = tasks;
        this.habits = habits;
    }

    @Override
    public void run(String... args) {
        if (tasks.count() == 0) {
            LocalDate today = LocalDate.now();
            tasks.save(new Task(null, "Konzept Modulprojekt abgeben", today, Priority.MUST_DO, false));
            tasks.save(new Task(null, "Wäsche waschen", today, Priority.NORMAL, true));
            tasks.save(new Task(null, "Einkaufen für die Woche", today, Priority.NORMAL, false));
            tasks.save(new Task(null, "Statistik: Übungsblatt lösen", today.plusDays(1), Priority.MUST_DO, false));
            tasks.save(new Task(null, "Controlling-Folien durchgehen", today.plusDays(2), Priority.NORMAL, false));
            tasks.save(new Task(null, "Fahrradreifen aufpumpen", today.plusDays(3), Priority.OPTIONAL, false));
        }
        if (habits.count() == 0) {
            habits.save(new Habit(null, "2 Liter Wasser trinken", true));
            habits.save(new Habit(null, "20 Minuten lesen", true));
            habits.save(new Habit(null, "Spaziergang an der frischen Luft", true));
            habits.save(new Habit(null, "Vokabeln lernen", false));
            habits.save(new Habit(null, "Zimmer aufräumen", false));
        }
    }
}