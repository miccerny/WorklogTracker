package cz.timetracker.entity.repository;

import cz.timetracker.dto.TimerDTO;
import cz.timetracker.entity.TimerEntity;
import cz.timetracker.entity.enums.TimerType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TimerRepository extends JpaRepository<TimerEntity, Long> {

    boolean existByWorkLogIdAndStatus(Long id, TimerType timerType);


    Optional<TimerEntity> findFirstByWorkLogIdAndStatusOrderByStartedAtDesc(Long id, TimerType timerType);
}
