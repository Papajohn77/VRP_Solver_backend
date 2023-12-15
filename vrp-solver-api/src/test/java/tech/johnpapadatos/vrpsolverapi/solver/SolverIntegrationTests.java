package tech.johnpapadatos.vrpsolverapi.solver;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import tech.johnpapadatos.vrpsolverapi.model.ModelRepository;

@ActiveProfiles("test")
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "spring.datasource.url=jdbc:tc:postgresql:16:///vrpsolver" 
    }
)
class SolverIntegrationTests {

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
    @Sql("/sql/seed-data.sql")
    void testSolveModelIT() {
        // Solve the model
        Response response = given()
            .contentType(ContentType.JSON)
            .when()
            .get("/solve?model_id={id}", 1)
            .then()
            .extract().response();
        
        assertEquals(200, response.getStatusCode());

        assertEquals(
            4182, 
            response.jsonPath().getInt("solution.total_distance_meters")
        );

        List<List<Map<String, Object>>> routes = response.jsonPath().get("solution.routes");
        assertEquals(3, routes.size());

        List<Map<String, Object>> route_1 = routes.get(0);
        assertEquals("depot", route_1.get(0).get("name"));
        assertEquals("c2", route_1.get(1).get("name"));
        assertEquals("depot", route_1.get(2).get("name"));

        List<Map<String, Object>> route_2 = routes.get(1);
        assertEquals("depot", route_2.get(0).get("name"));
        assertEquals("c3", route_2.get(1).get("name"));
        assertEquals("c1", route_2.get(2).get("name"));
        assertEquals("depot", route_2.get(3).get("name"));

        List<Map<String, Object>> route_3 = routes.get(2);
        assertEquals("depot", route_3.get(0).get("name"));
        assertEquals("depot", route_3.get(1).get("name"));

        List<String> vehicles = response.jsonPath().getList("solution.vehicles");
        assertEquals(3, vehicles.size());
        assertEquals("v1", vehicles.get(0));
        assertEquals("v2", vehicles.get(1));
        assertEquals("v77", vehicles.get(2));
    }

    @Test
    void testSolveModelThatDoesNotExistIT() {
        // Solve the model
        Response response = given()
            .contentType(ContentType.JSON)
            .when()
            .get("/solve?model_id={id}", 1)
            .then()
            .extract().response();
        
            assertEquals(404, response.getStatusCode());
            assertEquals(
                "There is no model with id=" + 1,
                response.jsonPath().getString("detail")
            );
    }
}
