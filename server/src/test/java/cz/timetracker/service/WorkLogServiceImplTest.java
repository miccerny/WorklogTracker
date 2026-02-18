package cz.timetracker.service;

import cz.timetracker.dto.WorkLogDTO;
import cz.timetracker.dto.mapper.WorkLogMapper;
import cz.timetracker.entity.UserEntity;
import cz.timetracker.entity.WorkLogEntity;
import cz.timetracker.entity.repository.UserRespository;
import cz.timetracker.entity.repository.WorkLogRepository;
import cz.timetracker.service.exceptions.NotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link WorkLogServiceImpl}.
 * <p>
 * These tests use mocks to focus only on service logic.
 * </p>
 */
class WorkLogServiceImplTest {

    private AutoCloseable mocks;

    @Mock
    private WorkLogRepository workLogRepository;

    @Mock
    private WorkLogMapper workLogMapper;

    @Mock
    private UserRespository userRespository;

    private WorkLogServiceImpl workLogService;

    private UserEntity currentUser;
    private WorkLogDTO inputDTO;
    private WorkLogEntity inputEntity;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        workLogService = new WorkLogServiceImpl(workLogRepository, workLogMapper, userRespository);

        currentUser = new UserEntity("Tester", "tester@example.com", "encoded");
        currentUser.setId(5L);

        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken(currentUser.getUsername(), "pass", "ROLE_USER")
        );

        inputDTO = new WorkLogDTO(10L, "Backend", 100f, List.of());
        inputEntity = new WorkLogEntity();
        inputEntity.setId(10L);
        inputEntity.setWorkLogName("Backend");
        inputEntity.setHourlyRate(100f);
        inputEntity.setActivated(true);

        when(userRespository.findByUsername(currentUser.getUsername())).thenReturn(Optional.of(currentUser));
    }

    @AfterEach
    void tearDown() throws Exception {
        SecurityContextHolder.clearContext();
        mocks.close();
    }

    @Test
    void createWorkLog_savesWorkLogForCurrentUser() {
        WorkLogEntity saved = new WorkLogEntity();
        saved.setId(20L);
        saved.setOwner(currentUser);

        WorkLogDTO expected = new WorkLogDTO(20L, "Backend", 100f, List.of());

        when(workLogMapper.toEntity(inputDTO)).thenReturn(inputEntity);
        when(workLogRepository.save(inputEntity)).thenReturn(saved);
        when(workLogMapper.toDTO(saved)).thenReturn(expected);

        WorkLogDTO result = workLogService.createWorkLog(inputDTO);

        assertThat(result).isEqualTo(expected);
        assertThat(inputEntity.getOwner()).isEqualTo(currentUser);
        verify(workLogRepository).save(inputEntity);
    }

    @Test
    void getAllWorkLogs_returnsOnlyCurrentUserWorkLogs() {
        WorkLogEntity second = new WorkLogEntity();
        second.setId(11L);

        WorkLogDTO firstDto = new WorkLogDTO(10L, "Backend", 100f, List.of());
        WorkLogDTO secondDto = new WorkLogDTO(11L, "Frontend", 90f, List.of());

        when(workLogRepository.findByOwnerId(5L)).thenReturn(List.of(inputEntity, second));
        when(workLogMapper.toDTO(inputEntity)).thenReturn(firstDto);
        when(workLogMapper.toDTO(second)).thenReturn(secondDto);

        List<WorkLogDTO> result = workLogService.getAllWorkLogs();

        assertThat(result).containsExactly(firstDto, secondDto);
        verify(workLogRepository).findByOwnerId(5L);
    }

    @Test
    void updateWorkLog_deactivatesOldAndSavesNew() {
        WorkLogEntity existing = new WorkLogEntity();
        existing.setId(10L);
        existing.setActivated(true);
        existing.setOwner(currentUser);

        WorkLogDTO updatedDto = new WorkLogDTO(30L, "Updated", 150f, List.of());

        WorkLogEntity updatedEntity = new WorkLogEntity();
        updatedEntity.setId(30L);
        updatedEntity.setActivated(true);

        when(workLogRepository.findByIdAndOwnerId(10L, 5L)).thenReturn(Optional.of(existing));
        when(workLogMapper.toEntity(updatedDto)).thenReturn(updatedEntity);
        when(workLogRepository.save(updatedEntity)).thenReturn(updatedEntity);
        when(workLogMapper.toDTO(updatedEntity)).thenReturn(updatedDto);

        WorkLogDTO result = workLogService.updateWorkLog(updatedDto, 10L);

        assertThat(result).isEqualTo(updatedDto);
        assertThat(existing.isActivated()).isFalse();

        InOrder inOrder = Mockito.inOrder(workLogRepository);
        inOrder.verify(workLogRepository).save(existing);
        inOrder.verify(workLogRepository).save(updatedEntity);
    }

    @Test
    void deleteWorkLog_deletesOwnedWorkLog() {
        when(workLogRepository.findByIdAndOwnerId(10L, 5L)).thenReturn(Optional.of(inputEntity));

        workLogService.deleteWorkLog(10L);

        verify(workLogRepository).delete(inputEntity);
    }

    @Test
    void getAllWorkLogs_throwsAccessDeniedWhenAuthenticationMissing() {
        SecurityContextHolder.clearContext();

        assertThrows(AccessDeniedException.class, () -> workLogService.getAllWorkLogs());

        verify(userRespository, never()).findByUsername(org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void deleteWorkLog_throwsNotFoundWhenWorkLogNotOwned() {
        when(workLogRepository.findByIdAndOwnerId(10L, 5L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> workLogService.deleteWorkLog(10L));

        verify(workLogRepository, never()).delete(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void createWorkLog_usesUsernameFromSecurityContext() {
        when(workLogMapper.toEntity(inputDTO)).thenReturn(inputEntity);
        when(workLogRepository.save(inputEntity)).thenReturn(inputEntity);
        when(workLogMapper.toDTO(inputEntity)).thenReturn(inputDTO);

        workLogService.createWorkLog(inputDTO);

        ArgumentCaptor<String> usernameCaptor = ArgumentCaptor.forClass(String.class);
        verify(userRespository).findByUsername(usernameCaptor.capture());
        assertThat(usernameCaptor.getValue()).isEqualTo("tester@example.com");
    }

}