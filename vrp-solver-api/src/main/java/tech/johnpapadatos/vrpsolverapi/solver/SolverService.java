package tech.johnpapadatos.vrpsolverapi.solver;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.google.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import tech.johnpapadatos.vrpsolverapi.customer.Customer;
import tech.johnpapadatos.vrpsolverapi.depot.Depot;
import tech.johnpapadatos.vrpsolverapi.exception.BadRequestException;
import tech.johnpapadatos.vrpsolverapi.exception.NotFoundException;
import tech.johnpapadatos.vrpsolverapi.model.Model;
import tech.johnpapadatos.vrpsolverapi.model.ModelRepository;
import tech.johnpapadatos.vrpsolverapi.solver.googlemaps.DistanceMatrixGenerator;
import tech.johnpapadatos.vrpsolverapi.solver.pojos.CustomerPOJO;
import tech.johnpapadatos.vrpsolverapi.solver.pojos.DepotPOJO;
import tech.johnpapadatos.vrpsolverapi.solver.pojos.RoutePointPOJO;
import tech.johnpapadatos.vrpsolverapi.solver.pojos.VehiclePOJO;
import tech.johnpapadatos.vrpsolverapi.solver.schemas.SolutionDTO;
import tech.johnpapadatos.vrpsolverapi.solver.schemas.SolutionResponseDTO;
import tech.johnpapadatos.vrpsolverapi.vehicle.Vehicle;

@Service
public class SolverService {
    private final ModelRepository modelRepository;
    private final DistanceMatrixGenerator distanceMatrixGenerator;

    public SolverService(
        ModelRepository modelRepository,
        DistanceMatrixGenerator distanceMatrixGenerator
    ) {
        this.modelRepository = modelRepository;
        this.distanceMatrixGenerator = distanceMatrixGenerator;
    }

    public SolutionResponseDTO solveModel(int modelId) {
        Optional<Model> model = modelRepository.findById(modelId);
        if (!model.isPresent()) {
            throw new NotFoundException("There is no model with id=" + modelId);
        }

        Depot depot = model.get().getDepot();
        if (depot == null) {
            throw new BadRequestException("Failed to solve model! Depot is missing.");
        }

        List<Customer> customers = model.get().getCustomers();
        if (customers.isEmpty()) {
            throw new BadRequestException("Failed to solve model! No customers provided.");
        }

        List<Vehicle> vehicles = model.get().getVehicles();
        if (vehicles.isEmpty()) {
            throw new BadRequestException("Failed to solve model! No vehicles provided.");
        }

        DepotPOJO depotPOJO = new DepotPOJO(
            0, 
            depot.getName(), 
            depot.getLatitude(), 
            depot.getLongitude(), 
            depot.getAddress()
        );

        List<CustomerPOJO> customerPOJOs = createCustomerPOJOs(customers);

        List<VehiclePOJO> vehiclePOJOs = createVehiclePOJOs(vehicles, depotPOJO);

        List<LatLng> locations = createLocations(depot, customers);
        int[][] distanceMatrix = distanceMatrixGenerator
            .createDistanceMatrix(locations);

        int totalDistanceMeters = solveWithMinimumInsertions(vehiclePOJOs, customerPOJOs, distanceMatrix);

        return new SolutionResponseDTO(
            new SolutionDTO(
                totalDistanceMeters, 
                vehiclePOJOs.stream().map(v -> v.getRoute()).toList(), 
                vehiclePOJOs.stream().map(v -> v.getName()).toList()
            )
        );
    }

    List<CustomerPOJO> createCustomerPOJOs(List<Customer> customers) {
        List<CustomerPOJO> customerPOJOs = new ArrayList<>();

        Customer currentCustomer;
        for (int i = 0; i < customers.size(); i++) {
            currentCustomer = customers.get(i);
            customerPOJOs.add(
                new CustomerPOJO(
                    i+1, 
                    currentCustomer.getName(), 
                    currentCustomer.getLatitude(), 
                    currentCustomer.getLongitude(), 
                    currentCustomer.getAddress(), 
                    currentCustomer.getDemand()
                )
            );
        }

        return customerPOJOs;
    }

    List<VehiclePOJO> createVehiclePOJOs(
        List<Vehicle> vehicles, DepotPOJO depotPOJO
    ) {
        List<VehiclePOJO> vehiclePOJOs = new ArrayList<>();

        for (var vehicle : vehicles) {
            vehiclePOJOs.add(
                new VehiclePOJO(
                    vehicle.getName(), 
                    vehicle.getCapacity(), 
                    depotPOJO
                )
            );
        }

        return vehiclePOJOs;
    }

    List<LatLng> createLocations(Depot depot, List<Customer> customers) {
        List<LatLng> locations = new ArrayList<>();
        locations.add(new LatLng(depot.getLatitude(), depot.getLongitude()));

        for (var customer : customers) {
            locations.add(new LatLng(customer.getLatitude(), customer.getLongitude()));
        }

        return locations;
    }
    
    int solveWithMinimumInsertions(
        List<VehiclePOJO> vehiclePOJOs,
        List<CustomerPOJO> customerPOJOs,
        int[][] distanceMatrix
    ) {
        int totalDistanceMeters = 0;

        while (true) {
            int min = Integer.MAX_VALUE;
            int minPosition = -1;
            CustomerPOJO minCustomer = null;
            VehiclePOJO minVehicle = null;

            for (var vehicle : vehiclePOJOs) {
                List<RoutePointPOJO> currentVehiclesRoute = vehicle.getRoute();
                for (var customer : customerPOJOs) {
                    for (int pos = 1; pos < currentVehiclesRoute.size(); pos++) {
                        RoutePointPOJO previous = currentVehiclesRoute.get(pos - 1);
                        RoutePointPOJO next = currentVehiclesRoute.get(pos);
                        int additionalDuration = distanceMatrix[previous.getId()][customer.getId()] 
                                                 + distanceMatrix[customer.getId()][next.getId()] 
                                                 - distanceMatrix[previous.getId()][next.getId()];

                        if (customer.getDemand() > vehicle.getCapacity()) {
                            continue;
                        }

                        if (additionalDuration < min) {
                            min = additionalDuration;
                            minPosition = pos;
                            minCustomer = customer;
                            minVehicle = vehicle;
                        }
                    }
                }
            }

            if (min == Integer.MAX_VALUE) {
                break;
            }

            totalDistanceMeters += min;
            minVehicle.reduceCapacity(minCustomer.getDemand());
            minVehicle.insertInRoute(minPosition, minCustomer);
            customerPOJOs.remove(minCustomer);
        }

        if (!customerPOJOs.isEmpty()) {
            throw new BadRequestException("""
                Model infeasible! The model could not be solved because we 
                could not serve all of the customers due to capacity constraints.
            """);
        }

        return totalDistanceMeters;
    }
}
