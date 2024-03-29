package tech.johnpapadatos.vrpsolverapi.depot;

import java.util.Optional;

import org.springframework.stereotype.Service;

import tech.johnpapadatos.vrpsolverapi.depot.mappers.DepotDTOMapper;
import tech.johnpapadatos.vrpsolverapi.depot.schemas.DepotCreateRequestDTO;
import tech.johnpapadatos.vrpsolverapi.depot.schemas.DepotCreateResponseDTO;
import tech.johnpapadatos.vrpsolverapi.depot.schemas.DepotResponseDTO;
import tech.johnpapadatos.vrpsolverapi.exception.AlreadyExistsException;
import tech.johnpapadatos.vrpsolverapi.exception.NotFoundException;
import tech.johnpapadatos.vrpsolverapi.model.Model;
import tech.johnpapadatos.vrpsolverapi.model.ModelRepository;

@Service
public class DepotService {
    private final DepotRepository depotRepository;
    private final DepotDTOMapper depotDTOMapper;
    private final ModelRepository modelRepository;

    public DepotService(
        DepotRepository depotRepository,
        DepotDTOMapper depotDTOMapper, 
        ModelRepository modelRepository
    ) {
        this.depotRepository = depotRepository;
        this.depotDTOMapper = depotDTOMapper;
        this.modelRepository = modelRepository;
    }

    public DepotResponseDTO getDepotByModelId(int modelId) {
        Optional<Model> model = modelRepository.findById(modelId);
        if (!model.isPresent()) {
            throw new NotFoundException(
                "There is no model with id=" + modelId
            );
        }

        Depot depot = model.get().getDepot();
        return depot != null
            ? new DepotResponseDTO(depotDTOMapper.apply(depot))
            : new DepotResponseDTO(null);
    }

    public DepotCreateResponseDTO createDepot(
        DepotCreateRequestDTO depotToCreate
    ) {
        Optional<Model> model = modelRepository.findById(
            depotToCreate.modelId()
        );
        if (!model.isPresent()) {
            throw new NotFoundException(
                "There is no model with id=" + depotToCreate.modelId()
            );
        }
        if (model.get().getDepot() != null) {
            throw new AlreadyExistsException(
                "Failed to create depot! There is already a depot for the model with id=" + depotToCreate.modelId()
            );
        }

        Depot savedDepot = depotRepository.save(
            Depot.builder()
                .name(depotToCreate.name())
                .latitude(depotToCreate.latitude())
                .longitude(depotToCreate.longitude())
                .address(depotToCreate.address())
                .model(model.get())
                .build()
        );
        return new DepotCreateResponseDTO(savedDepot.getId());
    }

    public void deleteDepot(int id) {
        // Could check if exists
        depotRepository.deleteById(id);
    }
}
