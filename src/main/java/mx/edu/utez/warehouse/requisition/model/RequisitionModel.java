package mx.edu.utez.warehouse.requisition.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.warehouse.order_status.model.OrderStatusModel;
import mx.edu.utez.warehouse.warehouse.model.WarehouseModel;

import java.sql.Date;
import java.util.List;

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
    private Date orderDate;

    @Column(nullable = false, unique = true)
    @NotEmpty(message = "La código no puede estar vacía")
    @NotNull(message = "La código no puede ser nula")
    @Size(min = 3, max = 50, message = "El código debe tener entre 3 y 50 caracteres")
    @Pattern(regexp = "[A-Za-z0-9 ]+", message = "El código debe ser alfanumérico")
    private String code;

    @Column(nullable = false)
    @Positive(message = "El total monetario del pedido debe ser positivo")
    private Double totalAmount;

    @Column(nullable = false)
    @Positive(message = "El total de productos debe ser positivo")
    private Integer totalOfProducts;

    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private OrderStatusModel status;

    @Column(nullable = false)
    @Min(value = 1, message = "El tipo debe ser 1 o 2")
    @Max(value = 2, message = "El tipo debe ser 1 o 2")
    private Integer type;

    @OneToMany(mappedBy = "requisition")
    private List<RequisitionProductModel> requisitionProductModels;

    @ManyToOne
    @JoinColumn(name = "warehouse_id", nullable = false)
    private WarehouseModel warehouse;

}
