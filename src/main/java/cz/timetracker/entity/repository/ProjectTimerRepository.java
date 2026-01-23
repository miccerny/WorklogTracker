package cz.timetracker.entity.repository;

import cz.timetracker.entity.ProjectTimerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectTimerRepository extends JpaRepository<ProjectTimerEntity, Long> {

}
