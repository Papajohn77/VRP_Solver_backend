package tech.johnpapadatos.vrpsolverapi.vehicle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import tech.johnpapadatos.vrpsolverapi.vehicle.schemas.VehicleResponseDTO;
import tech.johnpapadatos.vrpsolverapi.vehicle.schemas.VehiclesResponseDTO;
import tech.johnpapadatos.vrpsolverapi.vehicle.schemas.VehicleCreateRequestDTO;
import tech.johnpapadatos.vrpsolverapi.vehicle.schemas.VehicleCreateResponseDTO;
import tech.johnpapadatos.vrpsolverapi.exception.AlreadyExistsException;
import tech.johnpapadatos.vrpsolverapi.exception.NotFoundException;
import tech.johnpapadatos.vrpsolverapi.model.Model;
import tech.johnpapadatos.vrpsolverapi.model.ModelRepository;

class VehicleServiceTest {
    private VehicleService underTest;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private ModelRepository modelRepository;

    @Captor
    private ArgumentCaptor<Vehicle> vehicleArgumentCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        underTest = new VehicleService(vehicleRepository, modelRepository);
    }

    @Test
    void testGetVehiclesByModelId() {
        // Given
        Vehicle vehicle_1 = Vehicle.builder()
            .id(1)
            .name("v1")
            .capacity(45)
            .build();
        
        Vehicle vehicle_2 = Vehicle.builder()
            .id(2)
            .name("v2")
            .capacity(60)
            .build();
        
        Model model_1 = Model.builder()
            .id(1)
            .name("model_1")
            .vehicles(List.of(vehicle_1, vehicle_2))
            .build();

        given(modelRepository.findById(model_1.getId()))
            .willReturn(Optional.of(model_1));
        
        // When
        VehiclesResponseDTO vehicles = underTest.getVehiclesByModelId(
            model_1.getId()
        );

        // Then
        List<VehicleResponseDTO> listOfVehicles = vehicles.vehicles();
        assertEquals(2, listOfVehicles.size());

        VehicleResponseDTO vehicle_1_actual = listOfVehicles.get(0);
        assertEquals(vehicle_1.getId(), vehicle_1_actual.id());
        assertEquals(vehicle_1.getName(), vehicle_1_actual.name());
        assertEquals(vehicle_1.getCapacity(), vehicle_1_actual.capacity());

        VehicleResponseDTO vehicle_2_actual = listOfVehicles.get(1);
        assertEquals(vehicle_2.getId(), vehicle_2_actual.id());
        assertEquals(vehicle_2.getName(), vehicle_2_actual.name());
        assertEquals(vehicle_2.getCapacity(), vehicle_2_actual.capacity());
    }

    @Test
    void testGetVehiclesByModelIdThatDoesNotExist() {
        // Given
        Integer modelId = 1;
        given(modelRepository.findById(modelId))
            .willReturn(Optional.empty());

        // When
        // Then
        assertThrows(
            NotFoundException.class, 
            () -> underTest.getVehiclesByModelId(modelId)
        );
    }

    @Test
    void testGetVehiclesByModelIdThatDoesNotHaveVehicles() {
        // Given
        Model model_1 = Model.builder()
            .id(1)
            .name("model_1")
            .vehicles(new ArrayList<>())
            .build();

        given(modelRepository.findById(model_1.getId()))
            .willReturn(Optional.of(model_1));

        // When
        VehiclesResponseDTO vehicles = underTest.getVehiclesByModelId(
            model_1.getId()
        );

        // Then
        assertTrue(vehicles.vehicles().isEmpty());
    }

    @Test
    void testCreateVehicle() {
        // Given
        Model model_1 = Model.builder()
            .id(1)
            .name("model_1")
            .build();

        VehicleCreateRequestDTO vehicleToCreate = new VehicleCreateRequestDTO(
            "v1", 
            45,
            model_1.getId()
        );

        given(modelRepository.findById(vehicleToCreate.modelId()))
            .willReturn(Optional.of(model_1));
        
        Vehicle vehicleSaved = Vehicle.builder()
            .id(1)
            .build();

        given(vehicleRepository.save(any()))
            .willReturn(vehicleSaved);

        // When
        VehicleCreateResponseDTO vehicle = underTest.createVehicle(
            vehicleToCreate
        );

        // Then
        then(vehicleRepository).should().save(vehicleArgumentCaptor.capture());
        Vehicle vehicleArgumentCaptorValue = vehicleArgumentCaptor.getValue();
        assertEquals(vehicleToCreate.name(), vehicleArgumentCaptorValue.getName());
        assertEquals(vehicleToCreate.capacity(), vehicleArgumentCaptorValue.getCapacity());
        assertEquals(model_1, vehicleArgumentCaptorValue.getModel());

        assertEquals(vehicleSaved.getId(), vehicle.id());
    }

    @Test
    void testCreateVehicleForModelThatDoesNotExist() {
        // Given
        VehicleCreateRequestDTO vehicleToCreate = new VehicleCreateRequestDTO(
            "c1", 
            45, 
            1
        );

        given(modelRepository.findById(vehicleToCreate.modelId()))
            .willReturn(Optional.empty());

        // When
        // Then
        assertThrows(
            NotFoundException.class, 
            () -> underTest.createVehicle(vehicleToCreate)
        );

        then(vehicleRepository).shouldHaveNoInteractions();
    }

    @Test
    void testCreateVehicleThatAlreadyExistsForGivenModel() {
        // Given
        Model model_1 = Model.builder()
            .id(1)
            .name("model_1")
            .build();

        VehicleCreateRequestDTO vehicleToCreate = new VehicleCreateRequestDTO(
            "c1", 
            45, 
            1
        );

        given(modelRepository.findById(vehicleToCreate.modelId()))
            .willReturn(Optional.of(model_1));
        
        given(vehicleRepository.findByNameAndModelId(
            vehicleToCreate.name(), vehicleToCreate.modelId()
        ))
            .willReturn(Optional.of(mock(Vehicle.class)));

        // When
        // Then
        assertThrows(
            AlreadyExistsException.class, 
            () -> underTest.createVehicle(vehicleToCreate)
        );

        then(vehicleRepository).should().findByNameAndModelId(
            vehicleToCreate.name(), vehicleToCreate.modelId()
        );
        then(vehicleRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void testDeleteVehicle() {
        // Given
        Integer vehicleId = 1;

        // When
        underTest.deleteVehicle(vehicleId);

        // Then
        then(vehicleRepository).should().deleteById(vehicleId);
    }
}
