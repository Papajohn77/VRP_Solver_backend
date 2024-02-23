package tech.johnpapadatos.vrpsolverapi.customer;

import java.util.Optional;

import org.springframework.stereotype.Service;

import tech.johnpapadatos.vrpsolverapi.customer.mappers.CustomerResponseDTOMapper;
import tech.johnpapadatos.vrpsolverapi.customer.schemas.CustomerCreateRequestDTO;
import tech.johnpapadatos.vrpsolverapi.customer.schemas.CustomerCreateResponseDTO;
import tech.johnpapadatos.vrpsolverapi.customer.schemas.CustomersResponseDTO;
import tech.johnpapadatos.vrpsolverapi.exception.AlreadyExistsException;
import tech.johnpapadatos.vrpsolverapi.exception.NotFoundException;
import tech.johnpapadatos.vrpsolverapi.model.Model;
import tech.johnpapadatos.vrpsolverapi.model.ModelRepository;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerResponseDTOMapper customerResponseDTOMapper;
    private final ModelRepository modelRepository;

    public CustomerService(
        CustomerRepository customerRepository,
        CustomerResponseDTOMapper customerResponseDTOMapper,
        ModelRepository modelRepository
    ) {
        this.customerRepository = customerRepository;
        this.customerResponseDTOMapper = customerResponseDTOMapper;
        this.modelRepository = modelRepository;
    }

    public CustomersResponseDTO getCustomersByModelId(int modelId) {
        Optional<Model> model = modelRepository.findById(modelId);
        if (!model.isPresent()) {
            throw new NotFoundException(
                "There is no model with id=" + modelId
            );
        }

        return new CustomersResponseDTO(
            model.get()
                .getCustomers()
                .stream()
                .map(customerResponseDTOMapper)
                .toList()
        );
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
}
