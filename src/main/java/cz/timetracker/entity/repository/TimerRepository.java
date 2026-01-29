package cz.timetracker.entity.repository;

import cz.timetracker.dto.TimerDTO;
import cz.timetracker.entity.TimerEntity;
import cz.timetracker.entity.enums.TimerType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimerRepository extends JpaRepository<TimerEntity, Long> {

    boolean existByWorkLogIdAndStatus(Long id, TimerType timerType);
}
