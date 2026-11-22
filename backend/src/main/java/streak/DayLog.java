package de.doday.backend.streak;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Abschluss eines vergangenen Tages: wie viele von wie vielen Gewohnheiten erledigt waren. */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DayLog {

    @Id
    private LocalDate logDate;

    private int completed;
    private int total;
}
