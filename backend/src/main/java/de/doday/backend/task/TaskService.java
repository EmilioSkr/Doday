package de.doday.backend.task;

import de.doday.backend.error.NotFoundException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;

/**
 * Hält die Aufgaben vorerst im Arbeitsspeicher. Sie gehen beim Neustart verloren.
 * Mit der Datenbank (Milestone 4) wird die Map durch ein Repository ersetzt.
 */
@Service
public class TaskService {

    private final Map<Long, Task> tasks = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong();

    public TaskService() {
        LocalDate today = LocalDate.now();
        save(new Task(null, "Konzept Modulprojekt abgeben", today, Priority.MUST_DO, false));
        save(new Task(null, "Wäsche waschen", today, Priority.NORMAL, true));
        save(new Task(null, "Einkaufen für die Woche", today, Priority.NORMAL, false));
        save(new Task(null, "Statistik: Übungsblatt lösen", today.plusDays(1), Priority.MUST_DO, false));
        save(new Task(null, "Controlling-Folien durchgehen", today.plusDays(2), Priority.NORMAL, false));
        save(new Task(null, "Fahrradreifen aufpumpen", today.plusDays(3), Priority.OPTIONAL, false));
    }

    public List<Task> findAll() {
        return tasks.values().stream().sorted(Comparator.comparing(Task::getId)).toList();
    }

    public Task findById(Long id) {
        Task task = tasks.get(id);
        if (task == null) {
            throw new NotFoundException("Aufgabe mit der ID " + id + " wurde nicht gefunden.");
        }
        return task;
    }

    public Task create(TaskRequest request) {
        Task task = new Task(
                null,
                request.title().trim(),
                request.dueDate(),
                request.priority(),
                Boolean.TRUE.equals(request.done()));
        return save(task);
    }

    public Task update(Long id, TaskRequest request) {
        Task existing = findById(id);
        boolean done = request.done() != null ? request.done() : existing.isDone();
        Task updated = new Task(id, request.title().trim(), request.dueDate(), request.priority(), done);
        tasks.put(id, updated);
        return updated;
    }

    public void delete(Long id) {
        findById(id);
        tasks.remove(id);
    }

    private Task save(Task task) {
        task.setId(idCounter.incrementAndGet());
        tasks.put(task.getId(), task);
        return task;
    }
}