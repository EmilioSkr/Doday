package de.doday.backend.task;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TaskController {

    @GetMapping(path = "/api/tasks")
    public ResponseEntity<List<Task>> getTasks() {
        final List<Task> tasks = List.of(
                new Task(1L, "Konzept Modulprojekt abgeben", LocalDate.of(2026, 10, 12), Priority.MUST_DO, false),
                new Task(2L, "Statistik: Übungsblatt lösen", LocalDate.of(2026, 10, 13), Priority.MUST_DO, false),
                new Task(3L, "Controlling-Folien durchgehen", LocalDate.of(2026, 10, 14), Priority.NORMAL, false),
                new Task(4L, "Wäsche waschen", LocalDate.of(2026, 10, 12), Priority.OPTIONAL, true)
        );
        return ResponseEntity.ok(tasks);
    }
}