package mx.edu.utez.warehouse.requisition.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.warehouse.order_status.model.OrderStatusModel;
import mx.edu.utez.warehouse.product.model.ProductModel;
import mx.edu.utez.warehouse.warehouse.model.WarehouseModel;

import java.sql.Date;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "requisition")
public class RequisitionModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Positive(message = "El id debe ser positivo")
    private Long id;

    @Column(nullable = false)
    @NotNull(message = "La fecha no puede ser nula")
    @NotEmpty(message = "La fecha no puede estar vacía")
    private Date orderDate;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    @Positive(message = "El de total monerario del pedido debe ser positivo")
    @NotNull(message = "El total monetario del pedido no puede ser nulo")
    @NotEmpty(message = "El total monetario del pedido no puede estar vacío")
    private Double totalAmount;

    @Column(nullable = false)
    @Positive(message = "El total de productos debe ser positivo")
    @NotNull(message = "El total de productos no puede ser nulo")
    @NotEmpty(message = "El total de productos no puede estar vacío")
    private Integer totalOfProducts;

    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private OrderStatusModel status;

    @Column(nullable = false)
    @Min(value = 1, message = "El tipo debe ser 1 o 2")
    @Max(value = 2, message = "El tipo debe ser 1 o 2")
    private Integer type;

    @OneToMany(mappedBy = "requisition")
    private List<RequisitionProduct> requisitionProducts;

    @ManyToOne
    @JoinColumn(name = "warehouse_id", nullable = false)
    private WarehouseModel warehouse;

}
