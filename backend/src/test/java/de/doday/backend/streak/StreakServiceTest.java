package de.doday.backend.streak;

import static org.assertj.core.api.Assertions.assertThat;

import de.doday.backend.settings.SettingsService;
import java.time.Clock;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class StreakServiceTest {

    @Autowired
    private StreakService streakService;

    @Autowired
    private SettingsService settingsService;

    @Autowired
    private DayLogRepository logs;

    @Autowired
    private Clock clock;

    private LocalDate today;

    @BeforeEach
    void setUp() {
        today = LocalDate.now(clock);
        // legt die Einstellungen an und markiert heute als bereits "gestartet"
        streakService.ensureCurrentDay();
    }

    private void log(int daysAgo, int completed, int total) {
        logs.save(new DayLog(today.minusDays(daysAgo), completed, total));
    }

    @Test
    void isFlameNeedsAtLeastTheRequiredNumberButNeverMoreThanAll() {
        assertThat(StreakService.isFlame(3, 5, 3)).isTrue();
        assertThat(StreakService.isFlame(2, 5, 3)).isFalse();
        assertThat(StreakService.isFlame(5, 5, 99)).isTrue();   // Einstellung größer als alle -> alle
        assertThat(StreakService.isFlame(4, 5, 99)).isFalse();
        assertThat(StreakService.isFlame(0, 0, 1)).isFalse();   // ohne Gewohnheiten keine Flamme
    }

    @Test
    void countsConsecutiveFlameDaysBeforeToday() {
        settingsService.updateRequiredHabits(3);
        log(1, 3, 5);
        log(2, 5, 5);
        log(3, 4, 5);
        log(4, 2, 5); // zu wenig: Serie endet hier

        StreakResponse streak = streakService.getStreak();

        // Heute sind 3 von 5 Beispielgewohnheiten erledigt: 3 Tage davor + heute
        assertThat(streak.flameToday()).isTrue();
        assertThat(streak.currentStreak()).isEqualTo(4);
        assertThat(streak.longestStreak()).isEqualTo(4);
    }

    @Test
    void missingDayBreaksTheStreak() {
        settingsService.updateRequiredHabits(1);
        log(1, 5, 5);
        // vorgestern fehlt komplett
        log(3, 5, 5);
        log(4, 5, 5);
        log(5, 5, 5);

        StreakResponse streak = streakService.getStreak();

        assertThat(streak.currentStreak()).isEqualTo(2);  // gestern + heute
        assertThat(streak.longestStreak()).isEqualTo(3);  // vor 5, 4 und 3 Tagen
    }

    @Test
    void streakIsNotBrokenWhileTodayIsStillOpen() {
        settingsService.updateRequiredHabits(5); // heute sind nur 3 von 5 erledigt
        log(1, 5, 5);
        log(2, 5, 5);

        StreakResponse streak = streakService.getStreak();

        assertThat(streak.flameToday()).isFalse();
        assertThat(streak.currentStreak()).isEqualTo(2);
    }

    @Test
    void weekHasSevenDaysEndingToday() {
        StreakResponse streak = streakService.getStreak();

        assertThat(streak.week()).hasSize(7);
        assertThat(streak.week().get(6).date()).isEqualTo(today);
        assertThat(streak.week().get(0).date()).isEqualTo(today.minusDays(6));
    }

    @Test
    void settingIsClampedToTheNumberOfHabits() {
        assertThat(settingsService.updateRequiredHabits(99).requiredHabits()).isEqualTo(5);
        assertThat(settingsService.updateRequiredHabits(2).requiredHabits()).isEqualTo(2);
    }
}
