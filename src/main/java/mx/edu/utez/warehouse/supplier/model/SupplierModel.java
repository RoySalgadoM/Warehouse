package mx.edu.utez.warehouse.supplier.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "supplier")
public class SupplierModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Positive(message = "El id debe ser positivo")
    private Long id;

    @Column(nullable = false, unique = true)
    @NotEmpty(message = "El identificador no puede estar vacío")
    @NotNull(message = "El identificador no puede ser nulo")
    @Size(min = 3, max = 10, message = "El identificador debe tener entre 3 y 10 caracteres")
    @Pattern(regexp = "[A-Za-z0-9 ]+", message = "El identificador debe ser alfanumérico")
    private String rfc;

    @Column(nullable = false, unique = true)
    @NotEmpty(message = "El identificador no puede estar vacío")
    @NotNull(message = "El identificador no puede ser nulo")
    @Size(min = 3, max = 10, message = "El identificador debe tener entre 3 y 10 caracteres")
    @Pattern(regexp = "[A-Za-z0-9 ]+", message = "El identificador debe ser alfanumérico")
    private String businessName;

    @Column(nullable = false, unique = true)
    @NotEmpty(message = "El identificador no puede estar vacío")
    @NotNull(message = "El identificador no puede ser nulo")
    @Size(min = 3, max = 10, message = "El identificador debe tener entre 3 y 10 caracteres")
    @Pattern(regexp = "[A-Za-z0-9 ]+", message = "El identificador debe ser alfanumérico")
    private String phone;

    @Column(nullable = false, unique = true)
    @NotEmpty(message = "El identificador no puede estar vacío")
    @NotNull(message = "El identificador no puede ser nulo")
    @Size(min = 3, max = 10, message = "El identificador debe tener entre 3 y 10 caracteres")
    @Email(message = "El correo debe ser válido")
    private String email;
}
