package tech.johnpapadatos.vrpsolverapi.vehicle;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {
    
    @Query(
        value = """
            SELECT *
            FROM vehicles
            WHERE name = :name AND model_id = :model_id
        """,
        nativeQuery = true
    )
    Optional<Vehicle> findByNameAndModelId(
        @Param("name") String name,
        @Param("model_id") Integer modelId
    );
}
