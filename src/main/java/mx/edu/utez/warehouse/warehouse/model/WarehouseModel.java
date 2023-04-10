package mx.edu.utez.warehouse.warehouse.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.warehouse.user.model.UserModel;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "warehouse")
public class WarehouseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Positive(message = "El id debe ser positivo")
    private Long id;

    @Column(nullable = false)
    @NotEmpty(message = "El nombre no puede estar vacío")
    @NotNull(message = "El nombre no puede ser nulo")
    @Size(min = 3, message = "El nombre debe tener mínimo 3 caracteres")
    @Pattern(regexp = "[A-Za-z0-9 ]+", message = "El nombre debe ser alfanumérico")
    private String name;
    @Column(nullable = false, unique = true)
    @NotEmpty(message = "El identificador no puede estar vacío")
    @NotNull(message = "El identificador no puede ser nulo")
    @Size(min = 3, message = "El identificador debe tener mínimo 3 caracteres")
    @Pattern(regexp = "[A-Za-z0-9 ]+", message = "El identificador debe ser alfanumérico")
    private String identifier;
    @Column(nullable = false)
    @NotEmpty(message = "La dirección no puede estar vacío")
    @NotNull(message = "La dirección no puede ser nulo")
    @Size(min = 3, max = 100, message = "La dirección debe tener entre 3 y 100 caracteres")
    @Pattern(regexp = "[A-Za-z0-9 ]+", message = "La dirección debe ser alfanumérica")
    private String address;

    @ManyToOne
    @JoinColumn(name="warehouse_user")
    private UserModel warehouser;

    @ManyToOne
    @JoinColumn(name="invoice_user")
    private UserModel invoicer;

    @Override
    public String toString() {
        return "WarehouseModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", identifier='" + identifier + '\'' +
                ", address=" + address + '\'' +
                '}';
    }
}
