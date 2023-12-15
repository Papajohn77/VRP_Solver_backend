package tech.johnpapadatos.vrpsolverapi.customer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import tech.johnpapadatos.vrpsolverapi.customer.schemas.CustomerCreateRequestDTO;
import tech.johnpapadatos.vrpsolverapi.customer.schemas.CustomerCreateResponseDTO;
import tech.johnpapadatos.vrpsolverapi.customer.schemas.CustomerResponseDTO;
import tech.johnpapadatos.vrpsolverapi.customer.schemas.CustomersResponseDTO;
import tech.johnpapadatos.vrpsolverapi.exception.AlreadyExistsException;
import tech.johnpapadatos.vrpsolverapi.exception.NotFoundException;
import tech.johnpapadatos.vrpsolverapi.model.Model;
import tech.johnpapadatos.vrpsolverapi.model.ModelRepository;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final ModelRepository modelRepository;

    public CustomerService(
        CustomerRepository customerRepository,
        ModelRepository modelRepository
    ) {
        this.customerRepository = customerRepository;
        this.modelRepository = modelRepository;
    }

    public CustomersResponseDTO getCustomersByModelId(int modelId) {
        Optional<Model> model = modelRepository.findById(modelId);
        if (!model.isPresent()) {
            throw new NotFoundException(
                "There is no model with id=" + modelId
            );
        }

        List<Customer> customers = model.get().getCustomers();
        return convertCustomersToCustomersResponseDTO(customers);
    }

    public CustomerCreateResponseDTO createCustomer(
        CustomerCreateRequestDTO customerToCreate
    ) {
        Optional<Model> model = modelRepository.findById(
            customerToCreate.modelId()
        );
        if (!model.isPresent()) {
            throw new NotFoundException(
                "There is no model with id=" + customerToCreate.modelId()
            );
        }

        Optional<Customer> customer = customerRepository.findByNameAndModelId(
            customerToCreate.name(), customerToCreate.modelId()
        );
        if (customer.isPresent()) {
            throw new AlreadyExistsException(
                "Failed to create customer! There is already a customer with that name in the selected model."
            );
        }

        Customer savedCustomer = customerRepository.save(
            Customer.builder()
                .name(customerToCreate.name())
                .demand(customerToCreate.demand())
                .latitude(customerToCreate.latitude())
                .longitude(customerToCreate.longitude())
                .address(customerToCreate.address())
                .model(model.get())
                .build()  
        );
        return new CustomerCreateResponseDTO(savedCustomer.getId());
    }

    public void deleteCustomer(int id) {
        // Could check if exists
        customerRepository.deleteById(id);
    }

    private CustomersResponseDTO convertCustomersToCustomersResponseDTO(
        List<Customer> customers
    ) {
        List<CustomerResponseDTO> customerResponses = new ArrayList<>();
        for (var customer : customers) {
            customerResponses.add(
                new CustomerResponseDTO(
                    customer.getId(), 
                    customer.getName(), 
                    customer.getDemand(), 
                    customer.getLatitude(), 
                    customer.getLongitude(), 
                    customer.getAddress()
                )
            );
        }
        return new CustomersResponseDTO(customerResponses);
    }
}
