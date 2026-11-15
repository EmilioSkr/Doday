package de.doday.backend.habit;

import de.doday.backend.error.NotFoundException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;

/** Hält die Gewohnheiten vorerst im Arbeitsspeicher (später: Datenbank). */
@Service
public class HabitService {

    private final Map<Long, Habit> habits = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong();

    public HabitService() {
        save(new Habit(null, "2 Liter Wasser trinken", true));
        save(new Habit(null, "20 Minuten lesen", true));
        save(new Habit(null, "Spaziergang an der frischen Luft", true));
        save(new Habit(null, "Vokabeln lernen", false));
        save(new Habit(null, "Zimmer aufräumen", false));
    }

    public List<Habit> findAll() {
        return habits.values().stream().sorted(Comparator.comparing(Habit::getId)).toList();
    }

    public Habit findById(Long id) {
        Habit habit = habits.get(id);
        if (habit == null) {
            throw new NotFoundException("Gewohnheit mit der ID " + id + " wurde nicht gefunden.");
        }
        return habit;
    }

    public Habit create(HabitRequest request) {
        return save(new Habit(null, request.name().trim(), Boolean.TRUE.equals(request.done())));
    }

    public Habit update(Long id, HabitRequest request) {
        Habit existing = findById(id);
        boolean done = request.done() != null ? request.done() : existing.isDone();
        Habit updated = new Habit(id, request.name().trim(), done);
        habits.put(id, updated);
        return updated;
    }

    public void delete(Long id) {
        findById(id);
        habits.remove(id);
    }

    private Habit save(Habit habit) {
        habit.setId(idCounter.incrementAndGet());
        habits.put(habit.getId(), habit);
        return habit;
    }
}