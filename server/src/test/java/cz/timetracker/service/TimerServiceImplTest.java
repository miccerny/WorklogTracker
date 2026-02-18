package cz.timetracker.service;

import cz.timetracker.dto.TimerDTO;
import cz.timetracker.dto.mapper.TimerMapper;
import cz.timetracker.entity.TimerEntity;
import cz.timetracker.entity.UserEntity;
import cz.timetracker.entity.WorkLogEntity;
import cz.timetracker.entity.enums.TimerType;
import cz.timetracker.entity.repository.TimerRepository;
import cz.timetracker.entity.repository.WorkLogRepository;
import cz.timetracker.service.exceptions.ConflictException;
import cz.timetracker.service.exceptions.NotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TimerServiceImplTest {

    private AutoCloseable mocks;

    @Mock
    private TimerRepository timerRepository;

    @Mock
    private TimerMapper timerMapper;

    @Mock
    private WorkLogRepository workLogRepository;

    @InjectMocks
    private TimerServiceImpl timerService;

    private WorkLogEntity workLog;

    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        workLog = new WorkLogEntity();
        workLog.setId(1L);
        workLog.setWorkLogName("Backend");
        workLog.setActivated(true);
        userEntity = new UserEntity("Tester", "tester@example.com", "encoded");
        userEntity.setId(5L);
     }

    @AfterEach
    void tearDown() throws Exception{
        mocks.close();
    }

    @Test
    void startTimer_createAndReturnRunningTimer() {
        TimerEntity saved = new TimerEntity();
        saved.setId(10L);
        saved.setStatus(TimerType.RUNNING);
        saved.setWorkLog(workLog);

        TimerDTO expected = new TimerDTO();
        expected.setId(10L);
        expected.setStatus(TimerType.RUNNING);
        expected.setWorkLogId(1L);

        when(timerRepository.existsByWorkLogIdAndStatus(1L, TimerType.RUNNING)).thenReturn(false);
        when(workLogRepository.findById(1L)).thenReturn(Optional.of(workLog));
        when(timerRepository.save(any(TimerEntity.class))).thenReturn(saved);
        when(timerMapper.toDTO(saved)).thenReturn(expected);

        TimerDTO result = timerService.startTimer(1L);

        assertThat(result).isEqualTo(expected);

        ArgumentCaptor<TimerEntity> timerCaptor = ArgumentCaptor.forClass(TimerEntity.class);
        verify(timerRepository).save(timerCaptor.capture());
        TimerEntity created = timerCaptor.getValue();

        assertThat(created.getStatus()).isEqualTo(TimerType.RUNNING);
        assertThat(created.getWorkLog()).isEqualTo(workLog);
        assertThat(created.getStartedAt()).isNotNull();
    }

    @Test
    void startTimer_throwsConflictWhenTimerAlreadyRunning() {
        when(timerRepository.existsByWorkLogIdAndStatus(1L, TimerType.RUNNING)).thenReturn(true);

        assertThrows(ConflictException.class, () -> timerService.startTimer(1L));

        verify(workLogRepository, never()).findById(any());
        verify(timerRepository, never()).save(any());
    }

    @Test
    void getAllTimers_returnsMappedTimers() {
        TimerEntity first = new TimerEntity();
        first.setId(1L);
        TimerEntity second = new TimerEntity();
        second.setId(2L);

        TimerDTO firstDto = new TimerDTO();
        firstDto.setId(1L);
        TimerDTO secondDto = new TimerDTO();
        secondDto.setId(2L);

        when(timerRepository.existsByWorkLogIdAndOwner(1L, userEntity)).thenReturn(true);
        when(timerRepository.findByWorkLogIdOrderByStartedAtDesc(1L)).thenReturn(List.of(first, second));
        when(timerMapper.toDTO(first)).thenReturn(firstDto);
        when(timerMapper.toDTO(second)).thenReturn(secondDto);

        List<TimerDTO> result = timerService.getAllTimers(1L);

        assertThat(result).containsExactly(firstDto, secondDto);
    }

    @Test
    void getAllTimers_throwsNotFoundWhenWorkLogHasNoTimers() {
        when(timerRepository.existsByWorkLogIdAndOwner(1L, userEntity)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> timerService.getAllTimers(1L));

        verify(timerRepository, never()).findByWorkLogIdOrderByStartedAtDesc(any());
    }

    @Test
    void stopTimer_stopsRunningTimerInSameDay() {
        LocalDateTime start = LocalDateTime.now().minusMinutes(20);

        TimerEntity running = new TimerEntity();
        running.setId(3L);
        running.setStatus(TimerType.RUNNING);
        running.setStartedAt(start);
        running.setWorkLog(workLog);

        TimerDTO expected = new TimerDTO();
        expected.setId(3L);
        expected.setStatus(TimerType.STOPPED);

        when(timerRepository.findLatestForWorkLog(1L, TimerType.RUNNING, userEntity))
                .thenReturn(Optional.of(running));
        when(workLogRepository.findById(1L)).thenReturn(Optional.of(workLog));
        when(timerRepository.save(any(TimerEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(timerMapper.toDTO(any(TimerEntity.class))).thenReturn(expected);

        TimerDTO result = timerService.stopTimer(1L);

        assertThat(result).isEqualTo(expected);
        assertThat(running.getStatus()).isEqualTo(TimerType.STOPPED);
        assertThat(running.getStoppedAt()).isNotNull();
        assertThat(running.getDurationInSeconds()).isPositive();
    }

    @Test
    void stopTimer_splitsTimerWhenCrossingMidnight() {
        LocalDateTime start = LocalDateTime.now().minusDays(1).withHour(23).withMinute(55).withSecond(0).withNano(0);

        TimerEntity running = new TimerEntity();
        running.setId(4L);
        running.setStatus(TimerType.RUNNING);
        running.setStartedAt(start);
        running.setWorkLog(workLog);

        TimerDTO expected = new TimerDTO();
        expected.setId(99L);
        expected.setStatus(TimerType.STOPPED);

        when(timerRepository.findLatestForWorkLog(1L, TimerType.RUNNING, userEntity))
                .thenReturn(Optional.of(running));
        when(workLogRepository.findById(1L)).thenReturn(Optional.of(workLog));
        when(timerRepository.save(any(TimerEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(timerMapper.toDTO(any(TimerEntity.class))).thenReturn(expected);

        timerService.stopTimer(1L);

        ArgumentCaptor<TimerEntity> timerCaptor = ArgumentCaptor.forClass(TimerEntity.class);
        verify(timerRepository, org.mockito.Mockito.times(2)).save(timerCaptor.capture());
        List<TimerEntity> savedTimers = timerCaptor.getAllValues();

        assertThat(savedTimers.get(0).getStatus()).isEqualTo(TimerType.STOPPED);
        assertThat(savedTimers.get(1).getStatus()).isEqualTo(TimerType.STOPPED);
        assertThat(savedTimers.get(0).getStoppedAt()).isEqualTo(start.toLocalDate().plusDays(1).atStartOfDay());
        assertThat(savedTimers.get(1).getStartedAt()).isEqualTo(start.toLocalDate().plusDays(1).atStartOfDay());
    }

    @Test
    void getActiveTimer_returnsRunningTimer() {
        TimerEntity running = new TimerEntity();
        running.setId(7L);
        running.setStatus(TimerType.RUNNING);

        TimerDTO expected = new TimerDTO();
        expected.setId(7L);
        expected.setStatus(TimerType.RUNNING);

        when(timerRepository.findLatestForWorkLog(1L, TimerType.RUNNING, userEntity))
                .thenReturn(Optional.of(running));
        when(timerMapper.toDTO(running)).thenReturn(expected);

        TimerDTO result = timerService.getActiveTimer(1L);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void stopActiveTimer_throwsConflictWhenTimerAlreadyStopped() {
        TimerEntity stopped = new TimerEntity();
        stopped.setId(5L);
        stopped.setStatus(TimerType.STOPPED);

        when(timerRepository.findById(5L)).thenReturn(Optional.of(stopped));

        assertThrows(ConflictException.class, () -> timerService.stopActiveTimer(5L));
    }

    @Test
    void stopActiveTimer_usesTimerWorkLogAndStopsTimer() {
        LocalDateTime startedAt = LocalDateTime.now().minusMinutes(10);

        TimerEntity running = new TimerEntity();
        running.setId(6L);
        running.setStatus(TimerType.RUNNING);
        running.setStartedAt(startedAt);
        running.setWorkLog(workLog);

        TimerDTO expected = new TimerDTO();
        expected.setId(6L);
        expected.setStatus(TimerType.STOPPED);

        when(timerRepository.findById(6L)).thenReturn(Optional.of(running));
        when(timerRepository.save(any(TimerEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(timerMapper.toDTO(any(TimerEntity.class))).thenReturn(expected);

        TimerDTO result = timerService.stopActiveTimer(6L);

        assertThat(result).isEqualTo(expected);
        verify(workLogRepository, never()).findById(eq(1L));
        assertThat(running.getStatus()).isEqualTo(TimerType.STOPPED);
        assertThat(running.getDurationInSeconds()).isPositive();
    }
}