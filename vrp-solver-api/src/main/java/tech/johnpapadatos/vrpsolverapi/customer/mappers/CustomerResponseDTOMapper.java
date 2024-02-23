package tech.johnpapadatos.vrpsolverapi.customer.mappers;

import java.util.function.Function;

import org.springframework.stereotype.Component;

import tech.johnpapadatos.vrpsolverapi.customer.Customer;
import tech.johnpapadatos.vrpsolverapi.customer.schemas.CustomerResponseDTO;

@Component
public class CustomerResponseDTOMapper implements Function<Customer, CustomerResponseDTO> {
    @Override
    public CustomerResponseDTO apply(Customer customer) {
        return new CustomerResponseDTO(
            customer.getId(), 
            customer.getName(), 
            customer.getDemand(), 
            customer.getLatitude(), 
            customer.getLongitude(), 
            customer.getAddress()
        );
    }
}
