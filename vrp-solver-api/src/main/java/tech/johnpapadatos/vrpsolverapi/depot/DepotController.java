package tech.johnpapadatos.vrpsolverapi.depot;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import tech.johnpapadatos.vrpsolverapi.depot.schemas.DepotCreateRequestDTO;
import tech.johnpapadatos.vrpsolverapi.depot.schemas.DepotCreateResponseDTO;
import tech.johnpapadatos.vrpsolverapi.depot.schemas.DepotResponseDTO;

@Tag(name = "Depots")
@RestController
@RequestMapping("depot")
public class DepotController {
    private final DepotService depotService;

    public DepotController(DepotService depotService) {
        this.depotService = depotService;
    }

    @GetMapping
    public DepotResponseDTO getDepotByModelId(
        @RequestParam("model_id") int modelId
    ) {
        return depotService.getDepotByModelId(modelId);
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public DepotCreateResponseDTO createDepot(
        @Valid @RequestBody DepotCreateRequestDTO depotToCreate
    ) {
        return depotService.createDepot(depotToCreate);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteDepot(@PathVariable int id) {
        depotService.deleteDepot(id);
    }
}
