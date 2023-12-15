package tech.johnpapadatos.vrpsolverapi.vehicle;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import tech.johnpapadatos.vrpsolverapi.model.ModelRepository;
import tech.johnpapadatos.vrpsolverapi.model.schemas.ModelCreateRequestDTO;
import tech.johnpapadatos.vrpsolverapi.vehicle.schemas.VehicleCreateRequestDTO;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "spring.datasource.url=jdbc:tc:postgresql:16:///vrpsolver" 
    }
)
class VehicleIntegrationTests {

    @LocalServerPort
    private Integer port;

    @Autowired
    private ModelRepository modelRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
    }

    @AfterEach
    void tearDown() {
        vehicleRepository.deleteAll();
        modelRepository.deleteAll();
    }

    @Test
    void testVehiclesIT() {
        // Create a model to add vehicles to
        ModelCreateRequestDTO modelToCreate = new ModelCreateRequestDTO("model_1");

        Response modelCreatedResponse = given()
            .contentType(ContentType.JSON)
            .and()
            .body(modelToCreate)
            .when()
            .post("/models")
            .then()
            .extract().response();
        
        assertEquals(201, modelCreatedResponse.statusCode());
        Integer createdModelId = modelCreatedResponse.jsonPath().getInt("id");

        // Create vehicle_1
        VehicleCreateRequestDTO vehicleToCreate_1 = new VehicleCreateRequestDTO(
            "c1", 
            45, 
            createdModelId
        );

        Response vehicleCreatedResponse_1 = given()
            .contentType(ContentType.JSON)
            .and()
            .body(vehicleToCreate_1)
            .when()
            .post("/vehicles")
            .then()
            .extract().response();
        
        assertEquals(201, vehicleCreatedResponse_1.statusCode());
        Integer createdVehicleId_1 = vehicleCreatedResponse_1.jsonPath().getInt("id");

        // Create vehicle_2
        VehicleCreateRequestDTO vehicleToCreate_2 = new VehicleCreateRequestDTO(
            "c2", 
            64, 
            createdModelId
        );

        Response vehicleCreatedResponse_2 = given()
            .contentType(ContentType.JSON)
            .and()
            .body(vehicleToCreate_2)
            .when()
            .post("/vehicles")
            .then()
            .extract().response();
        
        assertEquals(201, vehicleCreatedResponse_2.statusCode());
        Integer createdVehicleId_2 = vehicleCreatedResponse_2.jsonPath().getInt("id");

        // Get the vehicles
        Response vehiclesResponse = given()
            .contentType(ContentType.JSON)
            .when()
            .get("/vehicles?model_id={id}", createdModelId)
            .then()
            .extract().response();
        
        assertEquals(200, vehiclesResponse.statusCode());

        List<Map<String, Object>> vehicles = vehiclesResponse.jsonPath().get("vehicles");
        assertEquals(2, vehicles.size());

        Map<String, Object> vehicle_1_actual = vehicles.get(0);
        assertEquals(createdVehicleId_1, vehicle_1_actual.get("id"));

        Map<String, Object> vehicle_2_actual = vehicles.get(1);
        assertEquals(createdVehicleId_2, vehicle_2_actual.get("id"));

        // Delete vehicle_1
        Response vehicle_1_DeletedResponse = given()
            .contentType(ContentType.JSON)
            .when()
            .delete("/vehicles/{id}", createdVehicleId_1)
            .then()
            .extract().response();
        
        assertEquals(204, vehicle_1_DeletedResponse.statusCode());

        // Get the vehicles and confirm that vehicle_1 has been deleted
        Response vehiclesResponse_retry = given()
            .contentType(ContentType.JSON)
            .when()
            .get("/vehicles?model_id={id}", createdModelId)
            .then()
            .extract().response();
        
        assertEquals(200, vehiclesResponse_retry.statusCode());

        List<Map<String, Object>> vehicles_retry = vehiclesResponse_retry.jsonPath().get("vehicles");
        assertEquals(1, vehicles_retry.size());

        Map<String, Object> vehicle_2_actual_retry = vehicles_retry.get(0);
        assertEquals(createdVehicleId_2, vehicle_2_actual_retry.get("id"));
    }
}
