package de.doday.backend.task;

import de.doday.backend.error.NotFoundException;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

    private final TaskRepository repo;

    public TaskService(TaskRepository repo) {
        this.repo = repo;
    }

    public List<Task> findAll() {
        return repo.findAll(Sort.by("id"));
    }

    public Task findById(Long id) {
        return repo.findById(id).orElseThrow(
                () -> new NotFoundException("Aufgabe mit der ID " + id + " wurde nicht gefunden."));
    }

    public Task create(TaskRequest request) {
        return repo.save(new Task(
                null,
                request.title().trim(),
                request.dueDate(),
                request.priority(),
                Boolean.TRUE.equals(request.done())));
    }

    public Task update(Long id, TaskRequest request) {
        Task existing = findById(id);
        existing.setTitle(request.title().trim());
        existing.setDueDate(request.dueDate());
        existing.setPriority(request.priority());
        if (request.done() != null) {
            existing.setDone(request.done());
        }
        return repo.save(existing);
    }

    public void delete(Long id) {
        findById(id);
        repo.deleteById(id);
    }
}