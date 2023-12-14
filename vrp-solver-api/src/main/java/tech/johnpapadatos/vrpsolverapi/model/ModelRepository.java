package tech.johnpapadatos.vrpsolverapi.model;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ModelRepository extends JpaRepository<Model, Integer> {
    Optional<Model> findByName(String name);
}
