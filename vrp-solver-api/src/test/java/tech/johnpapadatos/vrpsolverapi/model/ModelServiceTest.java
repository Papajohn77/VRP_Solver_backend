package tech.johnpapadatos.vrpsolverapi.model;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import tech.johnpapadatos.vrpsolverapi.exception.AlreadyExistsException;
import tech.johnpapadatos.vrpsolverapi.model.mappers.ModelResponseDTOMapper;
import tech.johnpapadatos.vrpsolverapi.model.schemas.ModelCreateRequestDTO;
import tech.johnpapadatos.vrpsolverapi.model.schemas.ModelCreateResponseDTO;
import tech.johnpapadatos.vrpsolverapi.model.schemas.ModelResponseDTO;
import tech.johnpapadatos.vrpsolverapi.model.schemas.ModelsResponseDTO;

class ModelServiceTest {
    private ModelService underTest;

    @Mock
    private ModelRepository modelRepository;

    @Captor
    private ArgumentCaptor<Model> modelArgumentCaptor;

    private final ModelResponseDTOMapper modelResponseDTOMapper 
        = new ModelResponseDTOMapper();

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        underTest = new ModelService(modelRepository, modelResponseDTOMapper);
    }

    @Test
    void testGetModels() {
        // Given
        Model model_1 = Model.builder().id(1).name("model_1").build();
        Model model_2 = Model.builder().id(2).name("model_2").build();
        given(modelRepository.findAll())
            .willReturn(List.of(model_1, model_2));

        // When
        ModelsResponseDTO models = underTest.getModels();

        // Then
        List<ModelResponseDTO> listOfModels = models.models();
        assertEquals(2, listOfModels.size());

        ModelResponseDTO model_1_actual = listOfModels.get(0);
        assertEquals(model_1.getId(), model_1_actual.id());
        assertEquals(model_1.getName(), model_1_actual.name());

        ModelResponseDTO model_2_actual = listOfModels.get(1);
        assertEquals(model_2.getId(), model_2_actual.id());
        assertEquals(model_2.getName(), model_2_actual.name());
    }

    @Test
    void testGetModelsEmptyList() {
        // Given
        given(modelRepository.findAll())
            .willReturn(Collections.emptyList());

        // When
        ModelsResponseDTO models = underTest.getModels();

        // Then
        List<ModelResponseDTO> listOfModels = models.models();
        assertEquals(0, listOfModels.size());
    }

    @Test
    void testCreateModel() {
        // Given
        ModelCreateRequestDTO modelToCreate = new ModelCreateRequestDTO(
            "model_1"
        );

        given(modelRepository.findByName(modelToCreate.name()))
            .willReturn(Optional.empty());

        Model modelSaved = Model.builder().id(1).name(modelToCreate.name()).build();
        given(modelRepository.save(any()))
            .willReturn(modelSaved);

        // When
        ModelCreateResponseDTO model = underTest.createModel(modelToCreate);

        // Then
        then(modelRepository).should().save(modelArgumentCaptor.capture());
        Model modelArgumentCaptorValue = modelArgumentCaptor.getValue();
        assertEquals(modelToCreate.name(), modelArgumentCaptorValue.getName());

        assertEquals(modelSaved.getId(), model.id());
    }

    @Test
    void testCreateModelAlreadyExists() {
        // Given
        ModelCreateRequestDTO modelToCreate = new ModelCreateRequestDTO(
            "model_1"
        );

        given(modelRepository.findByName(modelToCreate.name()))
            .willReturn(Optional.of(mock(Model.class)));

        // When
        // Then
        assertThrows(
            AlreadyExistsException.class, 
            () -> underTest.createModel(modelToCreate)
        );

        then(modelRepository).should(never()).save(any());
    }
}
