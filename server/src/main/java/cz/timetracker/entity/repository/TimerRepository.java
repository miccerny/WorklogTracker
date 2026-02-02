package cz.timetracker.entity.repository;

import cz.timetracker.entity.TimerEntity;
import cz.timetracker.entity.WorkLogEntity;
import cz.timetracker.entity.enums.TimerType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TimerRepository extends JpaRepository<TimerEntity, Long> {

    boolean existsByWorkLogIdAndStatus(Long id, TimerType timerType);
    boolean existsByWorkLogId(Long id);

    Optional<TimerEntity> findFirstByWorkLogIdAndStatusOrderByStartedAtDesc(Long id, TimerType timerType);


    List<TimerEntity> findByWorkLogIdOrderByStartedAtDesc(Long id);
}
