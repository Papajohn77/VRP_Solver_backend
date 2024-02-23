package tech.johnpapadatos.vrpsolverapi.model.mappers;

import java.util.function.Function;

import org.springframework.stereotype.Component;

import tech.johnpapadatos.vrpsolverapi.model.Model;
import tech.johnpapadatos.vrpsolverapi.model.schemas.ModelResponseDTO;

@Component
public class ModelResponseDTOMapper implements Function<Model, ModelResponseDTO> {
    @Override
    public ModelResponseDTO apply(Model model) {
        return new ModelResponseDTO(
            model.getId(), 
            model.getName()
        );
    }
}
