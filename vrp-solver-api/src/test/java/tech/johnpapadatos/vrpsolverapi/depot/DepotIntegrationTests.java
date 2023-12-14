package tech.johnpapadatos.vrpsolverapi.depot;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

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
import tech.johnpapadatos.vrpsolverapi.depot.schemas.DepotCreateRequestDTO;
import tech.johnpapadatos.vrpsolverapi.model.ModelRepository;
import tech.johnpapadatos.vrpsolverapi.model.schemas.ModelCreateRequestDTO;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "spring.datasource.url=jdbc:tc:postgresql:16:///vrpsolver" 
    }
)
class DepotIntegrationTests {

    @LocalServerPort
    private Integer port;

    @Autowired
    private ModelRepository modelRepository;

    @Autowired
    private DepotRepository depotRepository;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
    }

    @AfterEach
    void tearDown() {
        depotRepository.deleteAll();
        modelRepository.deleteAll();
    }

    @Test
    void testDepotIT() {
        // Create depot
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

        DepotCreateRequestDTO depotToCreate = new DepotCreateRequestDTO(
            "depot", 
            38.1234, 
            23.8123, 
            "Mesogeion 308", 
            createdModelId
        );

        Response depotCreatedResponse = given()
            .contentType(ContentType.JSON)
            .and()
            .body(depotToCreate)
            .when()
            .post("/depot")
            .then()
            .extract().response();
        
        assertEquals(201, depotCreatedResponse.statusCode());
        Integer createdDepotId = depotCreatedResponse.jsonPath().getInt("id");
        
        // Get the depot
        Response getCreatedDepotResponse = given()
            .contentType(ContentType.JSON)
            .when()
            .get("/depot?model_id={id}", createdModelId)
            .then()
            .extract().response();
        
        assertEquals(200, getCreatedDepotResponse.statusCode());

        Map<String, Object> depot = getCreatedDepotResponse.jsonPath().get("depot");
        assertEquals(createdDepotId, depot.get("id"));

        // Delete the depot
        Response depotDeletedResponse = given()
            .contentType(ContentType.JSON)
            .when()
            .delete("/depot/{id}", createdDepotId)
            .then()
            .extract().response();
        
        assertEquals(204, depotDeletedResponse.statusCode());
    
        // Attempt to get the depot again
        Response getCreatedDepotNotFoundResponse = given()
            .contentType(ContentType.JSON)
            .when()
            .get("/depot?model_id={id}", createdModelId)
            .then()
            .extract().response();
        
        assertEquals(404, getCreatedDepotNotFoundResponse.statusCode());
        assertEquals(
            "There is no depot for the model with id=" + createdModelId,
            getCreatedDepotNotFoundResponse.jsonPath().getString("detail")
        );
    }
}
