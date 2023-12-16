package tech.johnpapadatos.vrpsolverapi.solver;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import tech.johnpapadatos.vrpsolverapi.solver.schemas.SolutionResponseDTO;

@Tag(name = "Solver")
@RestController
@RequestMapping("/solve")
public class SolverController {
    private final SolverService solverService;

    public SolverController(SolverService solverService) {
        this.solverService = solverService;
    }

    @GetMapping
    public SolutionResponseDTO solveModel(
        @RequestParam("model_id") int modelId
    ) {
        return solverService.solveModel(modelId);
    }
}
