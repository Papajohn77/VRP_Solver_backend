package tech.johnpapadatos.vrpsolverapi.model;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import tech.johnpapadatos.vrpsolverapi.exception.AlreadyExistsException;
import tech.johnpapadatos.vrpsolverapi.model.mappers.ModelResponseDTOMapper;
import tech.johnpapadatos.vrpsolverapi.model.schemas.ModelCreateRequestDTO;
import tech.johnpapadatos.vrpsolverapi.model.schemas.ModelCreateResponseDTO;
import tech.johnpapadatos.vrpsolverapi.model.schemas.ModelsResponseDTO;

@Service
public class ModelService {
    private final ModelRepository modelRepository;
    private final ModelResponseDTOMapper modelResponseDTOMapper;

    public ModelService(
        ModelRepository modelRepository,
        ModelResponseDTOMapper modelResponseDTOMapper
    ) {
        this.modelRepository = modelRepository;
        this.modelResponseDTOMapper = modelResponseDTOMapper;
    }

    public ModelsResponseDTO getModels() {
        List<Model> models = modelRepository.findAll();

        return new ModelsResponseDTO(
            models.stream()
                .map(modelResponseDTOMapper)
                .toList()
        );
    }

    public ModelCreateResponseDTO createModel(
        ModelCreateRequestDTO modelToCreate
    ) {
        Optional<Model> modelInDatabase = modelRepository.findByName(
            modelToCreate.name()
        );
        if (modelInDatabase.isPresent()) {
            throw new AlreadyExistsException(
                "Failed to create model! There is already a model using that name."
            );
        }

        Model savedModel = modelRepository.save(
            Model.builder().name(modelToCreate.name()).build()
        );

        return new ModelCreateResponseDTO(savedModel.getId());
    }
}
