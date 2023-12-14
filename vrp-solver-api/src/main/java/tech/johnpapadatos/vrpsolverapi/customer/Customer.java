package tech.johnpapadatos.vrpsolverapi.customer;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import tech.johnpapadatos.vrpsolverapi.model.Model;

@Entity
@Table(name = "customers")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@EqualsAndHashCode
@ToString
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int demand;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    @Column(nullable = false)
    private String address;

    @ManyToOne
    @JoinColumn(
        name = "model_id", 
        referencedColumnName = "id", 
        nullable = false
    )
    private Model model;
}
