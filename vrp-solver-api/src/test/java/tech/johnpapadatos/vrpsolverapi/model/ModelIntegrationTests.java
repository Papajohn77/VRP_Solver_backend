package tech.johnpapadatos.vrpsolverapi.model;

import static org.junit.Assert.assertEquals;
import static io.restassured.RestAssured.given;

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
import tech.johnpapadatos.vrpsolverapi.model.schemas.ModelCreateRequestDTO;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "spring.datasource.url=jdbc:tc:postgresql:16:///vrpsolver" 
    }
)
class ModelIntegrationTests {

    @LocalServerPort
    private Integer port;

    @Autowired
    private ModelRepository modelRepository;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
    }

    @AfterEach
    void tearDown() {
        modelRepository.deleteAll();
    }

    @Test
    void testCreateAndGetModels() {
        // Create model_1
        ModelCreateRequestDTO modelToCreate_1 = new ModelCreateRequestDTO(
            "model_1"
        );

        Response modelCreatedResponse_1 = given()
            .contentType(ContentType.JSON)
            .and()
            .body(modelToCreate_1)
            .when()
            .post("/models")
            .then()
            .extract().response();
        
        assertEquals(201, modelCreatedResponse_1.statusCode());
        Integer createdModelId_1 = modelCreatedResponse_1.jsonPath().getInt("id");

        // Create model_2
        ModelCreateRequestDTO modelToCreate_2 = new ModelCreateRequestDTO(
            "model_2"
        );

        Response modelCreatedResponse_2 = given()
            .contentType(ContentType.JSON)
            .and()
            .body(modelToCreate_2)
            .when()
            .post("/models")
            .then()
            .extract().response();
        
        assertEquals(201, modelCreatedResponse_2.statusCode());
        Integer createdModelId_2 = modelCreatedResponse_2.jsonPath().getInt("id");

        // Get models
        Response response = given()
            .contentType(ContentType.JSON)
            .when()
            .get("/models")
            .then()
            .extract().response();

        assertEquals(200, response.statusCode());

        List<Map<String, Object>> models = response.jsonPath().get("models");
        assertEquals(2, models.size());

        Map<String, Object> model_1_actual = models.get(0);
        assertEquals(createdModelId_1, model_1_actual.get("id"));

        Map<String, Object> model_2_actual = models.get(1);
        assertEquals(createdModelId_2, model_2_actual.get("id"));
    }
}
