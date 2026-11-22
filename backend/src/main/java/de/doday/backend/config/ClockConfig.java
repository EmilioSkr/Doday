package de.doday.backend.config;

import java.time.Clock;
import java.time.ZoneId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Einheitliche Uhr für alles, was vom aktuellen Tag abhängt (Streaks, Tageswechsel).
 * Die Zeitzone ist über {@code doday.timezone} einstellbar, damit der Tag auch auf einem
 * Server in UTC (z. B. Render) um Mitternacht deutscher Zeit wechselt.
 */
@Configuration
public class ClockConfig {

    @Bean
    public Clock clock(@Value("${doday.timezone:Europe/Berlin}") String zone) {
        return Clock.system(ZoneId.of(zone));
    }
}
