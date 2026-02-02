package cz.timetracker.entity.repository;

import cz.timetracker.entity.WorkLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkLogRepository extends JpaRepository<WorkLogEntity, Long> {

}
