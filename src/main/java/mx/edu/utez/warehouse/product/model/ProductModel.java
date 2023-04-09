package mx.edu.utez.warehouse.product.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.warehouse.requisition.model.RequisitionProduct;

import java.sql.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product")
public class ProductModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Positive(message = "El id debe ser positivo")
    private Long id;

    @Column(nullable = false)
    @NotEmpty(message = "La descripción no puede estar vacía")
    @NotNull(message = "La descripción no puede ser nula")
    @Size(min = 5, max = 45, message = "La descripción debe tener entre 5 y 45 caracteres")
    @Pattern(regexp = "[A-Za-z0-9 ]+", message = "La descripción debe ser alfanumérica")
    private String description;
    @Column(nullable = false)
    @NotEmpty(message = "El nombre no puede estar vacía")
    @NotNull(message = "El nombre no puede ser nula")
    @Size(min = 5, max = 45, message = "El nombre debe tener entre 5 y 45 caracteres")
    @Pattern(regexp = "[A-Za-z0-9 ]+", message = "El nombre debe ser alfanumérica")
    private String name;


    @Column(nullable = false)
    @NotEmpty(message = "El tipo de unidad no puede estar vacío")
    @NotNull(message = "El tipo de unidad no puede ser nulo")
    @Size(min = 5, max = 45, message = "El tipo de unidad debe tener entre 5 y 45 caracteres")
    @Pattern(regexp = "[A-Za-z0-9 ]+", message = "El tipo de unidad debe ser alfanumérico")
    private String unit;

    @Column(nullable = false)
    @NotEmpty(message = "El precio no puede estar vacío")
    @NotNull(message = "El precio no puede ser nulo")
    @Min(value = 1, message = "El precio debe ser mayor a 0")
    private Double unitPrice;

    @Column(nullable = true)
    @NotEmpty(message = "El tipo de unidad no puede estar vacío")
    private Date expirationDate;

    @Column(nullable = true)
    @NotEmpty(message = "El tipo de unidad no puede estar vacío")
    @Size(min = 5, max = 45, message = "El tipo de unidad debe tener entre 5 y 45 caracteres")
    @Pattern(regexp = "[A-Za-z0-9 ]+", message = "El tipo de unidad debe ser alfanumérico")
    private String serialNumber;

    @Min(value = 1, message = "El tipo debe ser 1 o 2")
    @Max(value = 2, message = "El tipo debe ser 1 o 2")
    private Integer type;

    @Column(nullable = false)
    @Min(value = 0, message = "El status debe ser 0 o 1")
    @Max(value = 1, message = "El status debe ser 0 o 1")
    private Integer status;

    @OneToMany(mappedBy = "product")
    private List<RequisitionProduct> requisitionProducts;

    @Override
    public String toString() {
        return "ProductModel{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", name='" + name + '\'' +
                ", unit='" + unit + '\'' +
                ", unitPrice=" + unitPrice +
                ", expirationDate=" + expirationDate +
                ", serialNumber='" + serialNumber + '\'' +
                ", type='" + type + '\'' +
                ", status=" + status +
                '}';
    }
}
