package de.doday.backend.habit;

import de.doday.backend.error.NotFoundException;
import de.doday.backend.streak.StreakService;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class HabitService {

    private final HabitRepository repo;
    private final StreakService streakService;

    public HabitService(HabitRepository repo, StreakService streakService) {
        this.repo = repo;
        this.streakService = streakService;
    }

    public List<Habit> findAll() {
        streakService.ensureCurrentDay();
        return repo.findAll(Sort.by("id"));
    }

    public Habit findById(Long id) {
        streakService.ensureCurrentDay();
        return repo.findById(id).orElseThrow(
                () -> new NotFoundException("Gewohnheit mit der ID " + id + " wurde nicht gefunden."));
    }

    public Habit create(HabitRequest request) {
        streakService.ensureCurrentDay();
        return repo.save(new Habit(null, request.name().trim(), Boolean.TRUE.equals(request.done())));
    }

    public Habit update(Long id, HabitRequest request) {
        Habit existing = findById(id);
        existing.setName(request.name().trim());
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
