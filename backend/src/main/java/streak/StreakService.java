package de.doday.backend.streak;

import de.doday.backend.habit.Habit;
import de.doday.backend.habit.HabitRepository;
import de.doday.backend.settings.AppSettings;
import de.doday.backend.settings.SettingsService;
import de.doday.backend.streak.StreakResponse.DayStatus;
import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StreakService {

    private static final int WEEK_DAYS = 7;

    private final HabitRepository habits;
    private final DayLogRepository logs;
    private final SettingsService settingsService;
    private final Clock clock;

    public StreakService(HabitRepository habits, DayLogRepository logs,
                         SettingsService settingsService, Clock clock) {
        this.habits = habits;
        this.logs = logs;
        this.settingsService = settingsService;
        this.clock = clock;
    }

    /**
     * Tageswechsel: Ist seit dem letzten Aufruf ein neuer Tag angebrochen, wird der alte Tag
     * in {@link DayLog} festgehalten und alle Gewohnheiten werden auf "nicht erledigt" gesetzt.
     * Tage ohne Aufruf bekommen keinen Eintrag und zählen damit als verpasst.
     */
    @Transactional
    public void ensureCurrentDay() {
        LocalDate today = LocalDate.now(clock);
        AppSettings settings = settingsService.get();
        LocalDate last = settings.getLastRolloverDate();

        if (last == null) {
            settings.setLastRolloverDate(today);
            settingsService.save(settings);
            return;
        }
        if (!last.isBefore(today)) {
            return;
        }

        List<Habit> all = habits.findAll();
        int done = (int) all.stream().filter(Habit::isDone).count();
        logs.save(new DayLog(last, done, all.size()));

        all.forEach(habit -> habit.setDone(false));
        habits.saveAll(all);

        settings.setLastRolloverDate(today);
        settingsService.save(settings);
    }

    @Transactional
    public StreakResponse getStreak() {
        ensureCurrentDay();

        LocalDate today = LocalDate.now(clock);
        int required = settingsService.get().getRequiredHabits();
        List<Habit> all = habits.findAll();
        int total = all.size();
        int doneToday = (int) all.stream().filter(Habit::isDone).count();
        boolean flameToday = isFlame(doneToday, total, required);

        // Alle Tage mit Flamme. Vergangene Tage werden mit der aktuellen Einstellung bewertet.
        Set<LocalDate> flameDays = new TreeSet<>();
        for (DayLog log : logs.findAll()) {
            if (isFlame(log.getCompleted(), log.getTotal(), required)) {
                flameDays.add(log.getLogDate());
            }
        }
        if (flameToday) {
            flameDays.add(today);
        }

        // Aktuelle Serie: Ist heute noch keine Flamme da, ist die Serie nicht gerissen,
        // sie zählt dann bis gestern. Gerissen ist sie erst, wenn der Tag ohne Flamme endet.
        int current = 0;
        LocalDate cursor = flameToday ? today : today.minusDays(1);
        while (flameDays.contains(cursor)) {
            current++;
            cursor = cursor.minusDays(1);
        }

        int longest = 0;
        int run = 0;
        LocalDate previous = null;
        for (LocalDate day : flameDays) {
            run = previous != null && previous.plusDays(1).equals(day) ? run + 1 : 1;
            longest = Math.max(longest, run);
            previous = day;
        }

        List<DayStatus> week = new ArrayList<>(WEEK_DAYS);
        for (int i = WEEK_DAYS - 1; i >= 0; i--) {
            LocalDate day = today.minusDays(i);
            week.add(new DayStatus(day, flameDays.contains(day)));
        }

        int effectiveRequired = SettingsService.effective(required, total);
        return new StreakResponse(current, longest, flameToday, doneToday, total, effectiveRequired, week);
    }

    /** Ein Tag bekommt die Flamme, wenn mindestens so viele Gewohnheiten erledigt sind wie nötig (höchstens alle). */
    static boolean isFlame(int completed, int total, int required) {
        return total > 0 && completed >= Math.max(1, Math.min(required, total));
    }
}
