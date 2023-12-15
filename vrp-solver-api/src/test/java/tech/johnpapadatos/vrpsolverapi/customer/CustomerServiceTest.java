package tech.johnpapadatos.vrpsolverapi.customer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import tech.johnpapadatos.vrpsolverapi.customer.schemas.CustomerCreateRequestDTO;
import tech.johnpapadatos.vrpsolverapi.customer.schemas.CustomerCreateResponseDTO;
import tech.johnpapadatos.vrpsolverapi.customer.schemas.CustomerResponseDTO;
import tech.johnpapadatos.vrpsolverapi.customer.schemas.CustomersResponseDTO;
import tech.johnpapadatos.vrpsolverapi.exception.AlreadyExistsException;
import tech.johnpapadatos.vrpsolverapi.exception.NotFoundException;
import tech.johnpapadatos.vrpsolverapi.model.Model;
import tech.johnpapadatos.vrpsolverapi.model.ModelRepository;

class CustomerServiceTest {
    private CustomerService underTest;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ModelRepository modelRepository;

    @Captor
    private ArgumentCaptor<Customer> customerArgumentCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        underTest = new CustomerService(customerRepository, modelRepository);
    }

    @Test
    void testGetCustomersByModelId() {
        // Given
        Customer customer_1 = Customer.builder()
            .id(1)
            .name("c1")
            .demand(15)
            .latitude(38.1234)
            .longitude(23.8123)
            .address("Mesogeion 308")
            .build();
        
        Customer customer_2 = Customer.builder()
            .id(2)
            .name("c2")
            .demand(10)
            .latitude(38.1234)
            .longitude(23.8123)
            .address("Mesogeion 308")
            .build();
        
        Model model_1 = Model.builder()
            .id(1)
            .name("model_1")
            .customers(List.of(customer_1, customer_2))
            .build();

        given(modelRepository.findById(model_1.getId()))
            .willReturn(Optional.of(model_1));
        
        // When
        CustomersResponseDTO customers = underTest.getCustomersByModelId(
            model_1.getId()
        );

        // Then
        List<CustomerResponseDTO> listOfCustomers = customers.customers();
        assertEquals(2, listOfCustomers.size());

        CustomerResponseDTO customer_1_actual = listOfCustomers.get(0);
        assertEquals(customer_1.getId(), customer_1_actual.id());
        assertEquals(customer_1.getName(), customer_1_actual.name());
        assertEquals(customer_1.getDemand(), customer_1_actual.demand());
        assertEquals(customer_1.getLatitude(), customer_1_actual.latitude(), 0.001);
        assertEquals(customer_1.getLongitude(), customer_1_actual.longitude(), 0.001);
        assertEquals(customer_1.getAddress(), customer_1_actual.address());

        CustomerResponseDTO customer_2_actual = listOfCustomers.get(1);
        assertEquals(customer_2.getId(), customer_2_actual.id());
        assertEquals(customer_2.getName(), customer_2_actual.name());
        assertEquals(customer_2.getDemand(), customer_2_actual.demand());
        assertEquals(customer_2.getLatitude(), customer_2_actual.latitude(), 0.001);
        assertEquals(customer_2.getLongitude(), customer_2_actual.longitude(), 0.001);
        assertEquals(customer_2.getAddress(), customer_2_actual.address());
    }

    @Test
    void testGetCustomersByModelIdThatDoesNotExist() {
        // Given
        Integer modelId = 1;
        given(modelRepository.findById(modelId))
            .willReturn(Optional.empty());

        // When
        // Then
        assertThrows(
            NotFoundException.class, 
            () -> underTest.getCustomersByModelId(modelId)
        );
    }

    @Test
    void testGetCustomersByModelIdThatDoesNotHaveCustomers() {
        // Given
        Model model_1 = Model.builder()
            .id(1)
            .name("model_1")
            .customers(new ArrayList<>())
            .build();

        given(modelRepository.findById(model_1.getId()))
            .willReturn(Optional.of(model_1));

        // When
        CustomersResponseDTO customers = underTest.getCustomersByModelId(
            model_1.getId()
        );

        // Then
        assertTrue(customers.customers().isEmpty());
    }

    @Test
    void testCreateCustomer() {
        // Given
        Model model_1 = Model.builder()
            .id(1)
            .name("model_1")
            .build();

        CustomerCreateRequestDTO customerToCreate = new CustomerCreateRequestDTO(
            "c1", 
            12,
            38.1234, 
            23.8123, 
            "Mesogeion 308", 
            model_1.getId()
        );

        given(modelRepository.findById(customerToCreate.modelId()))
            .willReturn(Optional.of(model_1));
        
        Customer customerSaved = Customer.builder()
            .id(1)
            .build();

        given(customerRepository.save(any()))
            .willReturn(customerSaved);

        // When
        CustomerCreateResponseDTO customer = underTest.createCustomer(
            customerToCreate
        );

        // Then
        then(customerRepository).should().save(customerArgumentCaptor.capture());
        Customer customerArgumentCaptorValue = customerArgumentCaptor.getValue();
        assertEquals(customerToCreate.name(), customerArgumentCaptorValue.getName());
        assertEquals(customerToCreate.latitude(), customerArgumentCaptorValue.getLatitude(), 0.001);
        assertEquals(customerToCreate.longitude(), customerArgumentCaptorValue.getLongitude(), 0.001);
        assertEquals(customerToCreate.address(), customerArgumentCaptorValue.getAddress());
        assertEquals(model_1, customerArgumentCaptorValue.getModel());

        assertEquals(customerSaved.getId(), customer.id());
    }

    @Test
    void testCreateCustomerForModelThatDoesNotExist() {
        // Given
        CustomerCreateRequestDTO customerToCreate = new CustomerCreateRequestDTO(
            "c1", 
            12,
            38.1234, 
            23.8123, 
            "Mesogeion 308", 
            1
        );

        given(modelRepository.findById(customerToCreate.modelId()))
            .willReturn(Optional.empty());

        // When
        // Then
        assertThrows(
            NotFoundException.class, 
            () -> underTest.createCustomer(customerToCreate)
        );

        then(customerRepository).shouldHaveNoInteractions();
    }

    @Test
    void testCreateCustomerThatAlreadyExistsForGivenModel() {
        // Given
        Model model_1 = Model.builder()
            .id(1)
            .name("model_1")
            .build();

        CustomerCreateRequestDTO customerToCreate = new CustomerCreateRequestDTO(
            "c1", 
            12,
            38.1234, 
            23.8123, 
            "Mesogeion 308", 
            1
        );

        given(modelRepository.findById(customerToCreate.modelId()))
            .willReturn(Optional.of(model_1));
        
        given(customerRepository.findByNameAndModelId(
            customerToCreate.name(), customerToCreate.modelId()
        ))
            .willReturn(Optional.of(mock(Customer.class)));

        // When
        // Then
        assertThrows(
            AlreadyExistsException.class, 
            () -> underTest.createCustomer(customerToCreate)
        );

        then(customerRepository).should().findByNameAndModelId(
            customerToCreate.name(), customerToCreate.modelId()
        );
        then(customerRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void testDeleteCustomer() {
        // Given
        Integer customerId = 1;

        // When
        underTest.deleteCustomer(customerId);

        // Then
        then(customerRepository).should().deleteById(customerId);
    }
}
