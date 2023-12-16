package tech.johnpapadatos.vrpsolverapi.depot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import tech.johnpapadatos.vrpsolverapi.depot.schemas.DepotCreateRequestDTO;
import tech.johnpapadatos.vrpsolverapi.depot.schemas.DepotCreateResponseDTO;
import tech.johnpapadatos.vrpsolverapi.depot.schemas.DepotDTO;
import tech.johnpapadatos.vrpsolverapi.depot.schemas.DepotResponseDTO;
import tech.johnpapadatos.vrpsolverapi.exception.AlreadyExistsException;
import tech.johnpapadatos.vrpsolverapi.exception.NotFoundException;
import tech.johnpapadatos.vrpsolverapi.model.Model;
import tech.johnpapadatos.vrpsolverapi.model.ModelRepository;

class DepotServiceTest {
    private DepotService underTest;

    @Mock
    private DepotRepository depotRepository;

    @Mock
    private ModelRepository modelRepository;

    @Captor
    private ArgumentCaptor<Depot> depotArgumentCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        underTest = new DepotService(depotRepository, modelRepository);
    }

    @Test
    void testGetDepotByModelId() {
        // Given
        Depot model_1_depot = Depot.builder()
            .id(1)
            .name("depot")
            .latitude(38.1234)
            .longitude(23.8123)
            .address("Mesogeion 308")
            .build();

        Model model_1 = Model.builder()
            .id(1)
            .name("model_1")
            .depot(model_1_depot)
            .build();

        given(modelRepository.findById(model_1.getId()))
            .willReturn(Optional.of(model_1));

        // When
        DepotResponseDTO depot = underTest.getDepotByModelId(model_1.getId());

        // Then
        DepotDTO depotDTO = depot.depot();
        assertEquals(model_1_depot.getId(), depotDTO.id());
        assertEquals(model_1_depot.getName(), depotDTO.name());
        assertEquals(model_1_depot.getLatitude(), depotDTO.latitude(), 0.001);
        assertEquals(model_1_depot.getLongitude(), depotDTO.longitude(), 0.001);
        assertEquals(model_1_depot.getAddress(), depotDTO.address());
    }

    @Test
    void testGetDepotByModelIdThatDoesNotExist() {
        // Given
        Integer modelId = 1;
        given(modelRepository.findById(modelId))
            .willReturn(Optional.empty());

        // When
        // Then
        assertThrows(
            NotFoundException.class, 
            () -> underTest.getDepotByModelId(modelId)
        );
    }

    @Test
    void testGetDepotByModelIdThatDoesNotHaveDepot() {
        // Given
        Model model_1 = Model.builder()
            .id(1)
            .name("model_1")
            .build();

        given(modelRepository.findById(model_1.getId()))
            .willReturn(Optional.of(model_1));

        // When
        DepotResponseDTO depot = underTest.getDepotByModelId(model_1.getId());

        // Then
        assertNull(depot.depot());
    }

    @Test
    void testCreateDepot() {
        // Given
        Model model_1 = Model.builder()
            .id(1)
            .name("model_1")
            .build();

        DepotCreateRequestDTO depotToCreate = new DepotCreateRequestDTO(
            "depot", 
            38.1234, 
            23.8123, 
            "Mesogeion 308", 
            1
        );

        given(modelRepository.findById(depotToCreate.modelId()))
            .willReturn(Optional.of(model_1));
        
        Depot depotSaved = Depot.builder()
            .id(1)
            .build();

        given(depotRepository.save(any()))
            .willReturn(depotSaved);

        // When
        DepotCreateResponseDTO depot = underTest.createDepot(depotToCreate);

        // Then
        then(depotRepository).should().save(depotArgumentCaptor.capture());
        Depot depotArgumentCaptorValue = depotArgumentCaptor.getValue();
        assertEquals(depotToCreate.name(), depotArgumentCaptorValue.getName());
        assertEquals(depotToCreate.latitude(), depotArgumentCaptorValue.getLatitude(), 0.001);
        assertEquals(depotToCreate.longitude(), depotArgumentCaptorValue.getLongitude(), 0.001);
        assertEquals(depotToCreate.address(), depotArgumentCaptorValue.getAddress());
        assertEquals(model_1, depotArgumentCaptorValue.getModel());

        assertEquals(depotSaved.getId(), depot.id());
    }

    @Test
    void testCreateDepotForModelThatDoesNotExist() {
        // Given
        DepotCreateRequestDTO depotToCreate = new DepotCreateRequestDTO(
            "depot", 
            38.1234, 
            23.8123, 
            "Mesogeion 308", 
            1
        );

        given(modelRepository.findById(depotToCreate.modelId()))
            .willReturn(Optional.empty());

        // When
        // Then
        assertThrows(
            NotFoundException.class, 
            () -> underTest.createDepot(depotToCreate)
        );

        then(depotRepository).shouldHaveNoInteractions();
    }

    @Test
    void testCreateDepotForModelThatAlreadyHasDepot() {
        // Given
        Model model_1 = Model.builder()
            .id(1)
            .name("model_1")
            .depot(mock(Depot.class))
            .build();

        DepotCreateRequestDTO depotToCreate = new DepotCreateRequestDTO(
            "depot", 
            38.1234, 
            23.8123, 
            "Mesogeion 308", 
            1
        );

        given(modelRepository.findById(depotToCreate.modelId()))
            .willReturn(Optional.of(model_1));

        // When
        // Then
        assertThrows(
            AlreadyExistsException.class, 
            () -> underTest.createDepot(depotToCreate)
        );

        then(depotRepository).shouldHaveNoInteractions();
    }

    @Test
    void testDeleteDepot() {
        // Given
        Integer depotId = 1;

        // When
        underTest.deleteDepot(depotId);

        // Then
        then(depotRepository).should().deleteById(depotId);
    }
}
