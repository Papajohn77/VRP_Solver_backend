package tech.johnpapadatos.vrpsolverapi.customer;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import tech.johnpapadatos.vrpsolverapi.customer.schemas.CustomerCreateRequestDTO;
import tech.johnpapadatos.vrpsolverapi.customer.schemas.CustomerCreateResponseDTO;
import tech.johnpapadatos.vrpsolverapi.customer.schemas.CustomersResponseDTO;

@RestController
@RequestMapping("customers")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public CustomersResponseDTO getCustomersByModelId(
        @RequestParam("model_id") int modelId
    ) {
        return customerService.getCustomersByModelId(modelId);
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public CustomerCreateResponseDTO createCustomer(
        @Valid @RequestBody CustomerCreateRequestDTO customerToCreate
    ) {
        return customerService.createCustomer(customerToCreate);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteCustomer(@PathVariable int id) {
        customerService.deleteCustomer(id);
    }
}
