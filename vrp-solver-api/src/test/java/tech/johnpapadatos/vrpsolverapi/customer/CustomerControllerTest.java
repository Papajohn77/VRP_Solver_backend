package tech.johnpapadatos.vrpsolverapi.customer;

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
import tech.johnpapadatos.vrpsolverapi.customer.schemas.CustomerCreateRequestDTO;
import tech.johnpapadatos.vrpsolverapi.model.ModelRepository;
import tech.johnpapadatos.vrpsolverapi.model.schemas.ModelCreateRequestDTO;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "spring.datasource.url=jdbc:tc:postgresql:16:///vrpsolver" 
    }
)
class CustomerControllerTest {

    @LocalServerPort
    private Integer port;

    @Autowired
    private ModelRepository modelRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
    }

    @AfterEach
    void tearDown() {
        customerRepository.deleteAll();
        modelRepository.deleteAll();
    }

    @Test
    void testCustomersIT() {
        // Create a model to add customers to
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

        // Create customer_1
        CustomerCreateRequestDTO customerToCreate_1 = new CustomerCreateRequestDTO(
            "c1", 
            12, 
            38.1234, 
            23.8123, 
            "Mesogeion 308", 
            createdModelId
        );

        Response customerCreatedResponse_1 = given()
            .contentType(ContentType.JSON)
            .and()
            .body(customerToCreate_1)
            .when()
            .post("/customers")
            .then()
            .extract().response();
        
        assertEquals(201, customerCreatedResponse_1.statusCode());
        Integer createdCustomerId_1 = customerCreatedResponse_1.jsonPath().getInt("id");

        // Create customer_2
        CustomerCreateRequestDTO customerToCreate_2 = new CustomerCreateRequestDTO(
            "c2", 
            17, 
            38.1234, 
            23.8123, 
            "Mesogeion 308", 
            createdModelId
        );

        Response customerCreatedResponse_2 = given()
            .contentType(ContentType.JSON)
            .and()
            .body(customerToCreate_2)
            .when()
            .post("/customers")
            .then()
            .extract().response();
        
        assertEquals(201, customerCreatedResponse_2.statusCode());
        Integer createdCustomerId_2 = customerCreatedResponse_2.jsonPath().getInt("id");

        // Get the customers
        Response customersResponse = given()
            .contentType(ContentType.JSON)
            .when()
            .get("/customers?model_id={id}", createdModelId)
            .then()
            .extract().response();
        
        assertEquals(200, customersResponse.statusCode());

        List<Map<String, Object>> customers = customersResponse.jsonPath().get("customers");
        assertEquals(2, customers.size());

        Map<String, Object> customer_1_actual = customers.get(0);
        assertEquals(createdCustomerId_1, customer_1_actual.get("id"));

        Map<String, Object> customer_2_actual = customers.get(1);
        assertEquals(createdCustomerId_2, customer_2_actual.get("id"));

        // Delete customer_1
        Response customer_1_DeletedResponse = given()
            .contentType(ContentType.JSON)
            .when()
            .delete("/customers/{id}", createdCustomerId_1)
            .then()
            .extract().response();
        
        assertEquals(204, customer_1_DeletedResponse.statusCode());

        // Get the customers and confirm that customer_1 has been deleted
        Response customersResponse_retry = given()
            .contentType(ContentType.JSON)
            .when()
            .get("/customers?model_id={id}", createdModelId)
            .then()
            .extract().response();
        
        assertEquals(200, customersResponse_retry.statusCode());

        List<Map<String, Object>> customers_retry = customersResponse_retry.jsonPath().get("customers");
        assertEquals(1, customers_retry.size());

        Map<String, Object> customer_2_actual_retry = customers_retry.get(0);
        assertEquals(createdCustomerId_2, customer_2_actual_retry.get("id"));
    }
}
