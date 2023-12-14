package tech.johnpapadatos.vrpsolverapi.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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
class ModelControllerTest {

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
    void testGetModels() {
        // Given
        Model model_1 = new Model("model_1");
        Model model_2 = new Model("model_2");
        List<Model> modelsToBeSaved = List.of(model_1, model_2);
        modelRepository.saveAll(modelsToBeSaved);


        // When
        Response response = given()
            .contentType(ContentType.JSON)
            .when()
            .get("/models")
            .then()
            .extract().response();

        // Then
        assertEquals(200, response.statusCode());

        List<Map<String, Object>> models = response.jsonPath().get("models");
        assertEquals(2, models.size());

        Map<String, Object> model_1_actual = models.get(0);
        assertEquals(model_1.getId(), model_1_actual.get("id"));
        assertEquals(model_1.getName(), model_1_actual.get("name"));

        Map<String, Object> model_2_actual = models.get(1);
        assertEquals(model_2.getId(), model_2_actual.get("id"));
        assertEquals(model_2.getName(), model_2_actual.get("name"));
    }

    @Test
    void testCreateModel() {
        // Given
        ModelCreateRequestDTO requestBody = new ModelCreateRequestDTO(
            "model_1"
        );

        // When
        Response response = given()
            .contentType(ContentType.JSON)
            .and()
            .body(requestBody)
            .when()
            .post("/models")
            .then()
            .extract().response();
        
        // Then
        assertEquals(201, response.statusCode());
        assertTrue(response.jsonPath().getInt("id") >= 1);
    }

    @Test
    void testCreateModelWithBlankName() {
        // Given
        ModelCreateRequestDTO requestBody = new ModelCreateRequestDTO(
            ""
        );

        // When
        Response response = given()
            .contentType(ContentType.JSON)
            .and()
            .body(requestBody)
            .when()
            .post("/models")
            .then()
            .extract().response();
        
        // Then
        assertEquals(400, response.statusCode());
        assertEquals(
            "Model's name cannot be blank.",
            response.jsonPath().getString("detail")
        );
    }

    @Test
    void testCreateModelWithNameThatAlreadyExists() {
        // Given
        ModelCreateRequestDTO requestBody = new ModelCreateRequestDTO(
            "model_1"
        );

        // When
        given()
            .contentType(ContentType.JSON)
            .and()
            .body(requestBody)
            .when()
            .post("/models")
            .then()
            .statusCode(201);
        
        Response response = given()
            .contentType(ContentType.JSON)
            .and()
            .body(requestBody)
            .when()
            .post("/models")
            .then()
            .extract().response();
        
        // Then
        assertEquals(409, response.statusCode());
        assertEquals(
            "Failed to create model! There is already a model using that name.",
            response.jsonPath().getString("detail")
        );
    }
}
