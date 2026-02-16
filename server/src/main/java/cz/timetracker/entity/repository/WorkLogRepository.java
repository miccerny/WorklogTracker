package cz.timetracker.entity.repository;

import cz.timetracker.entity.UserEntity;
import cz.timetracker.entity.WorkLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkLogRepository extends JpaRepository<WorkLogEntity, Long> {
    List<WorkLogEntity> findByOwnerId(Long userId);
    Optional<WorkLogEntity> findByIdAndOwnerId(Long id, Long ownerId);
}
