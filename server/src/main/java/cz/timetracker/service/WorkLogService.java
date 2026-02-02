package cz.timetracker.service;

import cz.timetracker.dto.WorkLogDTO;

import java.util.List;

public interface WorkLogService {

    WorkLogDTO addWorkLog(WorkLogDTO projectTimer);
    List<WorkLogDTO> getAllWorkLogs();
    WorkLogDTO updateWorkLog(WorkLogDTO workLogDTO, Long id);
    void deleteWorkLog(Long id);
}
