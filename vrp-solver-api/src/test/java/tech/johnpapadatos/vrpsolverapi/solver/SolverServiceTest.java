package tech.johnpapadatos.vrpsolverapi.solver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.maps.model.LatLng;

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
import tech.johnpapadatos.vrpsolverapi.solver.schemas.SolutionResponseDTO;
import tech.johnpapadatos.vrpsolverapi.vehicle.Vehicle;

class SolverServiceTest {
    private SolverService underTest;

    @Mock
    private ModelRepository modelRepository;

    @Mock
    private DistanceMatrixGenerator distanceMatrixGenerator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        underTest = new SolverService(modelRepository, distanceMatrixGenerator);
    }

    @Test
    void testCreateCustomerPOJOs() {
        // Given
        Customer customer_1 = Customer.builder()
            .id(5) // This should become 1
            .name("c1")
            .demand(15)
            .latitude(38.1234)
            .longitude(23.8123)
            .address("Mesogeion 308")
            .build();
        
        Customer customer_2 = Customer.builder()
            .id(10) // This should become 2
            .name("c2")
            .demand(10)
            .latitude(38.4567)
            .longitude(23.8456)
            .address("Mesogeion 77")
            .build();

        List<Customer> customers = List.of(customer_1, customer_2);

        // When
        List<CustomerPOJO> customerPOJOs = underTest.createCustomerPOJOs(customers);

        // Then
        assertEquals(2, customerPOJOs.size());

        CustomerPOJO customer_1_POJO = customerPOJOs.get(0);
        assertEquals(1, customer_1_POJO.getId());
        assertEquals(customer_1.getName(), customer_1_POJO.getName());
        assertEquals(customer_1.getDemand(), customer_1_POJO.getDemand());
        assertEquals(customer_1.getLatitude(), customer_1_POJO.getLat(), 0.001);
        assertEquals(customer_1.getLongitude(), customer_1_POJO.getLng(), 0.001);
        assertEquals(customer_1.getAddress(), customer_1_POJO.getAddress());

        CustomerPOJO customer_2_POJO = customerPOJOs.get(1);
        assertEquals(2, customer_2_POJO.getId());
        assertEquals(customer_2.getName(), customer_2_POJO.getName());
        assertEquals(customer_2.getDemand(), customer_2_POJO.getDemand());
        assertEquals(customer_2.getLatitude(), customer_2_POJO.getLat(), 0.001);
        assertEquals(customer_2.getLongitude(), customer_2_POJO.getLng(), 0.001);
        assertEquals(customer_2.getAddress(), customer_2_POJO.getAddress());
    }

    @Test
    void testCreateVehiclePOJOs() {
        // Given
        DepotPOJO depotPOJOMock = mock(DepotPOJO.class);

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
        
        List<Vehicle> vehicles = List.of(vehicle_1, vehicle_2);

        // When
        List<VehiclePOJO> vehiclePOJOs = underTest.createVehiclePOJOs(
            vehicles,
            depotPOJOMock
        );

        // Then
        assertEquals(2, vehiclePOJOs.size());

        VehiclePOJO vehicle_1_POJO = vehiclePOJOs.get(0);
        assertEquals(vehicle_1.getName(), vehicle_1_POJO.getName());
        assertEquals(vehicle_1.getCapacity(), vehicle_1_POJO.getCapacity());
        List<RoutePointPOJO> routePointPOJOs_1 = vehicle_1_POJO.getRoute();
        assertEquals(2, routePointPOJOs_1.size());
        assertEquals(depotPOJOMock, routePointPOJOs_1.get(0));
        assertEquals(depotPOJOMock, routePointPOJOs_1.get(1));

        VehiclePOJO vehicle_2_POJO = vehiclePOJOs.get(1);
        assertEquals(vehicle_2.getName(), vehicle_2_POJO.getName());
        assertEquals(vehicle_2.getCapacity(), vehicle_2_POJO.getCapacity());
        List<RoutePointPOJO> routePointPOJOs_2 = vehicle_2_POJO.getRoute();
        assertEquals(2, routePointPOJOs_2.size());
        assertEquals(depotPOJOMock, routePointPOJOs_2.get(0));
        assertEquals(depotPOJOMock, routePointPOJOs_2.get(1));
    }

    @Test
    void testCreateLocations() {
        // Given
        Depot depot = Depot.builder()
            .id(1)
            .name("depot")
            .latitude(38.1234)
            .longitude(23.8123)
            .address("Mesogeion 308")
            .build();

        Customer customer_1 = Customer.builder()
            .id(1)
            .name("c1")
            .demand(15)
            .latitude(38.5678)
            .longitude(23.8567)
            .address("Mesogeion 79")
            .build();
        
        Customer customer_2 = Customer.builder()
            .id(2)
            .name("c2")
            .demand(10)
            .latitude(38.3241)
            .longitude(23.7892)
            .address("Mesogeion 7")
            .build();
        
        List<Customer> customers = List.of(customer_1, customer_2);

        // When
        List<LatLng> locations = underTest.createLocations(depot, customers);

        // Then
        assertEquals(3, locations.size());

        LatLng depotLatLng = locations.get(0);
        assertEquals(depot.getLatitude(), depotLatLng.lat, 0.001);
        assertEquals(depot.getLongitude(), depotLatLng.lng, 0.001);

        LatLng customer_1_LatLng = locations.get(1);
        assertEquals(customer_1.getLatitude(), customer_1_LatLng.lat, 0.001);
        assertEquals(customer_1.getLongitude(), customer_1_LatLng.lng, 0.001);

        LatLng customer_2_LatLng = locations.get(2);
        assertEquals(customer_2.getLatitude(), customer_2_LatLng.lat, 0.001);
        assertEquals(customer_2.getLongitude(), customer_2_LatLng.lng, 0.001);
    }

    @Test
    void testSolveWithMinimumInsertions() {
        // Given
        DepotPOJO depotPOJO = new DepotPOJO(
            0, 
            "depot", 
            37.9873, 
            23.7581, 
            "Λεωφόρος Αλεξάνδρας 203"
        );

        VehiclePOJO vehicle_1 = new VehiclePOJO(
            "v1", 
            20, 
            depotPOJO
        );

        VehiclePOJO vehicle_2 = new VehiclePOJO(
            "v2", 
            40, 
            depotPOJO
        );

        VehiclePOJO vehicle_3 = new VehiclePOJO(
            "v77", 
            10, 
            depotPOJO
        );

        List<VehiclePOJO> vehiclePOJOs = List.of(
            vehicle_1,
            vehicle_2,
            vehicle_3
        );

        CustomerPOJO customer_1 = new CustomerPOJO(
            1, 
            "c1", 
            37.9905, 
            23.7612, 
            "Αργολίδος 42", 
            14
        );

        CustomerPOJO customer_2 = new CustomerPOJO(
            2, 
            "c2", 
            37.9903, 
            23.7572, 
            "Αλφειού 7", 
            12
        );

        CustomerPOJO customer_3 = new CustomerPOJO(
            3, 
            "c3", 
            37.9924, 
            23.7545, 
            "Πριήνης 18", 
            8
        );

        List<CustomerPOJO> customerPOJOs = new ArrayList<>();
        customerPOJOs.add(customer_1);
        customerPOJOs.add(customer_2);
        customerPOJOs.add(customer_3);

        int[][] distanceMatrix = new int[][] {
            {0, 752, 593, 903},
            {911, 0, 1505, 1225},
            {800, 556, 0, 1306},
            {1456, 975, 1375, 0}
        };

        // When
        int totalDistanceMeters = underTest.solveWithMinimumInsertions(
            vehiclePOJOs, 
            customerPOJOs, 
            distanceMatrix
        );

        // Then
        assertEquals(4182, totalDistanceMeters);

        List<RoutePointPOJO> vehicle_1_Route = vehicle_1.getRoute();
        assertEquals(3, vehicle_1_Route.size());
        assertEquals(depotPOJO, vehicle_1_Route.get(0));
        assertEquals(customer_2, vehicle_1_Route.get(1));
        assertEquals(depotPOJO, vehicle_1_Route.get(2));

        List<RoutePointPOJO> vehicle_2_Route = vehicle_2.getRoute();
        assertEquals(4, vehicle_2_Route.size());
        assertEquals(depotPOJO, vehicle_2_Route.get(0));
        assertEquals(customer_3, vehicle_2_Route.get(1));
        assertEquals(customer_1, vehicle_2_Route.get(2));
        assertEquals(depotPOJO, vehicle_2_Route.get(3));

        List<RoutePointPOJO> vehicle_3_Route = vehicle_3.getRoute();
        assertEquals(2, vehicle_3_Route.size());
        assertEquals(depotPOJO, vehicle_3_Route.get(0));
        assertEquals(depotPOJO, vehicle_3_Route.get(1));
    }

    @Test
    void testSolveWithMinimumInsertionsInfeasibleModel() {
        // Given
        DepotPOJO depotPOJO = new DepotPOJO(
            0, 
            "depot", 
            37.9873, 
            23.7581, 
            "Λεωφόρος Αλεξάνδρας 203"
        );

        VehiclePOJO vehicle_1 = new VehiclePOJO(
            "v1", 
            15, 
            depotPOJO
        );

        VehiclePOJO vehicle_2 = new VehiclePOJO(
            "v2", 
            15, 
            depotPOJO
        );

        VehiclePOJO vehicle_3 = new VehiclePOJO(
            "v77", 
            5, 
            depotPOJO
        );

        List<VehiclePOJO> vehiclePOJOs = List.of(
            vehicle_1,
            vehicle_2,
            vehicle_3
        );

        CustomerPOJO customer_1 = new CustomerPOJO(
            1, 
            "c1", 
            37.9905, 
            23.7612, 
            "Αργολίδος 42", 
            14
        );

        CustomerPOJO customer_2 = new CustomerPOJO(
            2, 
            "c2", 
            37.9903, 
            23.7572, 
            "Αλφειού 7", 
            12
        );

        CustomerPOJO customer_3 = new CustomerPOJO(
            3, 
            "c3", 
            37.9924, 
            23.7545, 
            "Πριήνης 18", 
            8
        );

        List<CustomerPOJO> customerPOJOs = new ArrayList<>();
        customerPOJOs.add(customer_1);
        customerPOJOs.add(customer_2);
        customerPOJOs.add(customer_3);

        int[][] distanceMatrix = new int[][] {
            {0, 752, 593, 903},
            {911, 0, 1505, 1225},
            {800, 556, 0, 1306},
            {1456, 975, 1375, 0}
        };

        // When
        // Then
        // The capacity is not enough to serve all 3 customers.
        assertThrows(
            BadRequestException.class, 
            () -> underTest.solveWithMinimumInsertions(
                vehiclePOJOs, 
                customerPOJOs, 
                distanceMatrix
            )
        );
    }

    @Test
    void testSolveModel() {
        // Given
        Depot depot = Depot.builder()
            .id(1)
            .name("depot")
            .latitude(37.9873)
            .longitude(23.7581)
            .address("Λεωφόρος Αλεξάνδρας 203")
            .build();

        Vehicle vehicle_1 = Vehicle.builder()
            .id(1)
            .name("v1")
            .capacity(20)
            .build();

        Vehicle vehicle_2 = Vehicle.builder()
            .id(2)
            .name("v2")
            .capacity(40)
            .build();

        Vehicle vehicle_3 = Vehicle.builder()
            .id(3)
            .name("v77")
            .capacity(10)
            .build();

        Customer customer_1 = Customer.builder()
            .id(1)
            .name("c1")
            .demand(14)
            .latitude(37.9905)
            .longitude(23.7612)
            .address("Αργολίδος 42")
            .build();

        Customer customer_2 = Customer.builder()
            .id(2)
            .name("c2")
            .demand(12)
            .latitude(37.9903)
            .longitude(23.7572)
            .address("Αλφειού 7")
            .build();

        Customer customer_3 = Customer.builder()
            .id(3)
            .name("c3")
            .demand(8)
            .latitude(37.9924)
            .longitude(23.7545)
            .address("Πριήνης 18")
            .build();

        Model model = Model.builder()
            .id(1)
            .name("model_1")
            .depot(depot)
            .customers(List.of(customer_1, customer_2, customer_3))
            .vehicles(List.of(vehicle_1, vehicle_2, vehicle_3))
            .build();
        
        given(modelRepository.findById(model.getId()))
            .willReturn(Optional.of(model));

        int[][] distanceMatrix = new int[][] {
            {0, 752, 593, 903},
            {911, 0, 1505, 1225},
            {800, 556, 0, 1306},
            {1456, 975, 1375, 0}
        };

        given(distanceMatrixGenerator.createDistanceMatrix(anyList()))
            .willReturn(distanceMatrix);

        // When
        SolutionResponseDTO solution = underTest.solveModel(model.getId());

        // Then
        assertEquals(4182, solution.solution().totalDistanceMeters());

        List<RoutePointPOJO> route_1 = solution.solution().routes().get(0);
        assertEquals(3, route_1.size());
        assertEquals("depot", route_1.get(0).getName());
        assertEquals("c2", route_1.get(1).getName());
        assertEquals("depot", route_1.get(2).getName());

        List<RoutePointPOJO> route_2 = solution.solution().routes().get(1);
        assertEquals(4, route_2.size());
        assertEquals("depot", route_2.get(0).getName());
        assertEquals("c3", route_2.get(1).getName());
        assertEquals("c1", route_2.get(2).getName());
        assertEquals("depot", route_2.get(3).getName());

        List<RoutePointPOJO> route_3 = solution.solution().routes().get(2);
        assertEquals(2, route_3.size());
        assertEquals("depot", route_3.get(0).getName());
        assertEquals("depot", route_3.get(1).getName());

        List<String> vehicles = solution.solution().vehicles();
        assertEquals(vehicle_1.getName(), vehicles.get(0));
        assertEquals(vehicle_2.getName(), vehicles.get(1));
        assertEquals(vehicle_3.getName(), vehicles.get(2));
    }

    @Test
    void testSolveModelByModelIdThatDoesNotExists() {
        // Given
        Integer modelId = 1;
        given(modelRepository.findById(modelId))
            .willReturn(Optional.empty());

        // When
        // Then
        assertThrows(
            NotFoundException.class, 
            () -> underTest.solveModel(modelId)
        );
    }

    @Test
    void testSolveModelByModelThatDoesNotHaveDepot() {
        // Given
        Model model = Model.builder()
            .id(1)
            .name("model_1")
            .build();
        
        given(modelRepository.findById(model.getId()))
            .willReturn(Optional.of(model));
        
        // When
        // Then
        BadRequestException thrown = assertThrows(
            BadRequestException.class, 
            () -> underTest.solveModel(model.getId())
        );

        assertEquals(
            "Failed to solve model! Depot is missing.", 
            thrown.getMessage()
        );
    }

    @Test
    void testSolveModelByModelThatDoesNotHaveCustomers() {
        // Given
        Model model = Model.builder()
            .id(1)
            .name("model_1")
            .depot(mock(Depot.class))
            .customers(new ArrayList<>())
            .build();
        
        given(modelRepository.findById(model.getId()))
            .willReturn(Optional.of(model));
        
        // When
        // Then
        BadRequestException thrown = assertThrows(
            BadRequestException.class, 
            () -> underTest.solveModel(model.getId())
        );

        assertEquals(
            "Failed to solve model! No customers provided.", 
            thrown.getMessage()
        );
    }

    @Test
    void testSolveModelByModelThatDoesNotHaveVehicles() {
        // Given
        Model model = Model.builder()
            .id(1)
            .name("model_1")
            .depot(mock(Depot.class))
            .customers(List.of(mock(Customer.class)))
            .vehicles(new ArrayList<>())
            .build();

        given(modelRepository.findById(model.getId()))
            .willReturn(Optional.of(model));
        
        // When
        // Then
        BadRequestException thrown = assertThrows(
            BadRequestException.class, 
            () -> underTest.solveModel(model.getId())
        );

        assertEquals(
            "Failed to solve model! No vehicles provided.", 
            thrown.getMessage()
        );
    }
}
