package cz.timetracker.entity.repository;

import cz.timetracker.entity.TimerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimerRepository extends JpaRepository<TimerEntity, Long> {
}
