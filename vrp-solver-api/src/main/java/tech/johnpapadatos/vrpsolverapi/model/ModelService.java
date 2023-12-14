package tech.johnpapadatos.vrpsolverapi.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import tech.johnpapadatos.vrpsolverapi.exception.AlreadyExistsException;
import tech.johnpapadatos.vrpsolverapi.model.schemas.ModelCreateRequestDTO;
import tech.johnpapadatos.vrpsolverapi.model.schemas.ModelCreateResponseDTO;
import tech.johnpapadatos.vrpsolverapi.model.schemas.ModelResponseDTO;
import tech.johnpapadatos.vrpsolverapi.model.schemas.ModelsResponseDTO;

@Service
public class ModelService {
    private final ModelRepository modelRepository;

    public ModelService(ModelRepository modelRepository) {
        this.modelRepository = modelRepository;
    }

    public ModelsResponseDTO getModels() {
        List<Model> models = modelRepository.findAll();
        return convertModelsToModelsResponseDTO(models);
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

    private ModelsResponseDTO convertModelsToModelsResponseDTO(
        List<Model> models
    ) {
        List<ModelResponseDTO> modelResponses = new ArrayList<>();
        for (var model : models) {
            modelResponses.add(
                new ModelResponseDTO(model.getId(), model.getName())
            );
        }
        return new ModelsResponseDTO(modelResponses);
    }
}
