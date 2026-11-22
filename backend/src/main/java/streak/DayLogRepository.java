package de.doday.backend.streak;

import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DayLogRepository extends JpaRepository<DayLog, LocalDate> { }
