package tech.johnpapadatos.vrpsolverapi.model;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import tech.johnpapadatos.vrpsolverapi.model.schemas.ModelCreateRequestDTO;
import tech.johnpapadatos.vrpsolverapi.model.schemas.ModelCreateResponseDTO;
import tech.johnpapadatos.vrpsolverapi.model.schemas.ModelsResponseDTO;

@RestController
@RequestMapping("models")
public class ModelController {
    private final ModelService modelService;

    public ModelController(ModelService modelService) {
        this.modelService = modelService;
    }

    @GetMapping
    public ModelsResponseDTO getModels() {
        return modelService.getModels();
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public ModelCreateResponseDTO createModel(
        @Valid @RequestBody ModelCreateRequestDTO modelToCreate
    ) {
        return modelService.createModel(modelToCreate);
    }
}
