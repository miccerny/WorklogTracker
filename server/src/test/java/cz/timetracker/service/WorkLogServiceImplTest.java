package cz.timetracker.service;

import cz.timetracker.dto.WorkLogDTO;
import cz.timetracker.dto.mapper.WorkLogMapper;
import cz.timetracker.entity.WorkLogEntity;
import cz.timetracker.entity.repository.WorkLogRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

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

    private WorkLogServiceImpl workLogService;

    private WorkLogDTO inputDTO;
    private WorkLogDTO savedDTO;
    private WorkLogEntity inputEntity;
    private WorkLogEntity savedEntity;

    /**
     * Creates mocks and basic test data before each test.
     */
    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        workLogService = new WorkLogServiceImpl(workLogRepository,
                workLogMapper);

        inputDTO = new WorkLogDTO(10L, "First Work Log", 100f, List.of());
        savedDTO = new WorkLogDTO(11L, "Saved Work Log", 120f, List.of());

        inputEntity = new WorkLogEntity();
        inputEntity.setId(inputDTO.getId());
        inputEntity.setWorkLogName(inputDTO.getWorkLogName());
        inputEntity.setHourlyRate(inputDTO.getHourlyRate());
        inputEntity.setActivated(true);

        savedEntity = new WorkLogEntity();
        savedEntity.setId(savedDTO.getId());
        savedEntity.setWorkLogName(savedDTO.getWorkLogName());
        savedEntity.setHourlyRate(savedDTO.getHourlyRate());
        savedEntity.setActivated(true);
    }

    @AfterEach
    void tearDown() throws Exception{
        mocks.close();
    }

    @Test
    void createWorkLog_savesAndReturnDto() {
        when(workLogMapper.toEntity(inputDTO))
                .thenReturn(inputEntity);
        when(workLogRepository.save(inputEntity))
                .thenReturn(savedEntity);
        when(workLogMapper.toDTO(savedEntity))
                .thenReturn(savedDTO);
        WorkLogDTO result = workLogService
                .createWorkLog(inputDTO);

        assertThat(result).isEqualTo(savedDTO);
        verify(workLogMapper).toEntity(inputDTO);
        verify(workLogRepository).save(inputEntity);
        verify(workLogMapper).toDTO(savedEntity);
    }

    /**
     * Ensures getAllWorkLogs returns a list of mapped DTOs.
     */
    @Test
    void getAllWorkLogs_returnsMappedDtos() {
        WorkLogEntity secondEntity = new WorkLogEntity();
        secondEntity.setId(12L);
        secondEntity.setWorkLogName("Second Work Log");
        secondEntity.setHourlyRate(80f);
        secondEntity.setActivated(true);

        WorkLogDTO secondDto = new WorkLogDTO(12L, "Second Work Log", 80f, List.of());
        when(workLogRepository.findAll())
                .thenReturn(List.of(
                        inputEntity, secondEntity
                ));

        when(workLogMapper.toDTO(inputEntity))
                .thenReturn(inputDTO);
        when(workLogMapper.toDTO(secondEntity))
                .thenReturn(secondDto);

        List<WorkLogDTO> result = workLogService
                .getAllWorkLogs();

        assertThat(result).containsExactly(inputDTO, secondDto);
    }


    /**
     * Checks that updateWorkLog deactivates the old entity and saves the new one.
     */
    @Test
    void updateWorkLog_deactivatesOldAndSavesNew() {
        WorkLogEntity updatedEntity = new WorkLogEntity();
        updatedEntity.setId(20L);
        updatedEntity.setWorkLogName("Updated Work Long");
        updatedEntity.setHourlyRate(150f);
        updatedEntity.setActivated(true);

        WorkLogDTO updatedDto = new WorkLogDTO(
                20L, "Updated Work Log", 150f,
                List.of()
        );
        when(workLogRepository.findById(10L)).thenReturn(
                Optional.of(inputEntity)
        );
        when(workLogMapper.toEntity(updatedDto))
                .thenReturn(updatedEntity);
        when(workLogRepository.save(updatedEntity))
                .thenReturn(updatedEntity);
        when(workLogMapper.toDTO(updatedEntity)).thenReturn(
                updatedDto
        );

        WorkLogDTO result = workLogService.updateWorkLog(updatedDto, 10L);

        assertThat(inputEntity.isActivated()).isFalse();
        assertThat(result).isEqualTo(updatedDto);

        InOrder inOrder = Mockito.inOrder(workLogRepository);
        inOrder.verify(workLogRepository).save(inputEntity);
        inOrder.verify(workLogRepository).save(updatedEntity);
    }

    /**
     * Confirms deleteWorkLog removes the entity when found.
     */
    @Test
    void deleteWorkLog_removesEntity() {
        when(workLogRepository.findById(10L))
                .thenReturn(Optional.of(inputEntity));

        workLogService.deleteWorkLog(10L);
        verify(workLogRepository).delete(inputEntity);
    }
}