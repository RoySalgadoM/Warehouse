package mx.edu.utez.warehouse.requisition.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.warehouse.product.model.ProductModel;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "requisition_product")
public class RequisitionProductModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Positive(message = "El id debe ser positivo")
    private Long id;

    @Column(nullable = false)
    @Min(value = 1, message = "La cantidad de productos debe ser mayor a 0")
    private Integer quantity;

    @ManyToOne
    @JoinColumn(name = "requisition_id")
    private RequisitionModel requisition;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductModel product;
}
