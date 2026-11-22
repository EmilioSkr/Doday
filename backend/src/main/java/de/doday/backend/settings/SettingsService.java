package de.doday.backend.settings;

import de.doday.backend.habit.HabitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SettingsService {

    private final AppSettingsRepository repo;
    private final HabitRepository habits;

    public SettingsService(AppSettingsRepository repo, HabitRepository habits) {
        this.repo = repo;
        this.habits = habits;
    }

    /** Liefert die Einstellungen und legt sie beim ersten Aufruf an (Standard: alle aktuellen Gewohnheiten). */
    @Transactional
    public AppSettings get() {
        return repo.findById(AppSettings.SINGLETON_ID).orElseGet(() ->
                repo.save(new AppSettings(AppSettings.SINGLETON_ID, (int) Math.max(1, habits.count()), null)));
    }

    @Transactional
    public AppSettings save(AppSettings settings) {
        return repo.save(settings);
    }

    /** Setzt die Anzahl und begrenzt sie auf 1 bis zur aktuellen Anzahl der Gewohnheiten. */
    @Transactional
    public SettingsResponse updateRequiredHabits(int requested) {
        AppSettings settings = get();
        int total = (int) habits.count();
        settings.setRequiredHabits(effective(requested, total));
        repo.save(settings);
        return toResponse(settings);
    }

    @Transactional
    public SettingsResponse getResponse() {
        return toResponse(get());
    }

    /** Wirksamer Wert: nie kleiner als 1 und nie größer als die Anzahl der Gewohnheiten. */
    public static int effective(int required, int total) {
        return Math.max(1, Math.min(required, total));
    }

    private SettingsResponse toResponse(AppSettings settings) {
        int total = (int) habits.count();
        return new SettingsResponse(effective(settings.getRequiredHabits(), total), total);
    }
}
