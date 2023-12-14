package tech.johnpapadatos.vrpsolverapi.model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import tech.johnpapadatos.vrpsolverapi.customer.Customer;
import tech.johnpapadatos.vrpsolverapi.depot.Depot;
import tech.johnpapadatos.vrpsolverapi.vehicle.Vehicle;

@Entity
@Table(
    name = "models",
    uniqueConstraints = {
        @UniqueConstraint(name = "model_unique_name", columnNames = "name")
    }
)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@EqualsAndHashCode
@ToString
public class Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @OneToOne(mappedBy = "model")
    private Depot depot;

    @OneToMany(mappedBy = "model")
    private List<Customer> customers;

    @OneToMany(mappedBy = "model")
    private List<Vehicle> vehicles;
}
