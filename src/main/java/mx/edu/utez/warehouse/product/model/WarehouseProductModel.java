package mx.edu.utez.warehouse.product.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.warehouse.warehouse.model.WarehouseModel;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "warehouse_product")
public class WarehouseProductModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Positive(message = "El id debe ser positivo")
    private Long id;

    @Column(nullable = false)
    @Min(value = 1, message = "La cantidad de productos debe ser mayor a 0")
    private Integer quantity;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonIgnore
    private ProductModel product;

    @ManyToOne
    @JoinColumn(name = "warehouse_id")
    @JsonIgnore
    private WarehouseModel warehouse;
}
