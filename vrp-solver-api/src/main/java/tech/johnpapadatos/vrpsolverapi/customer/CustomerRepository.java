package tech.johnpapadatos.vrpsolverapi.customer;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    
    @Query(
        value = """
            SELECT *
            FROM customers
            WHERE name = :name AND model_id = :model_id        
        """,
        nativeQuery = true
    )
    Optional<Customer> findByNameAndModelId(
        @Param("name") String name,
        @Param("model_id") Integer modelId
    );
}
