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

    public Model () {}

    public Model(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Depot getDepot() {
        return depot;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Model other = (Model) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Model [id=" + id + ", name=" + name + "]";
    }
}
