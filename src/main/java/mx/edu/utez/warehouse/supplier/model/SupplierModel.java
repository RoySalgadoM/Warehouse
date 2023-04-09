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

    @Column(nullable = false)
    @NotEmpty(message = "El nombre no puede estar vacío")
    @NotNull(message = "El nombre no puede ser nulo")
    @Size(min = 5, max = 45, message = "El nombre debe tener entre 5 a 45 caracteres")
    @Pattern(regexp = "[A-Za-z ]+", message = "El nombre debe contener solo letras")
    private String name;

    @Column(nullable = false, unique = true)
    @NotEmpty(message = "El RFC no puede estar vacío")
    @NotNull(message = "El RFC no puede ser nulo")
    @Size(min = 5, max = 13, message = "El RFC debe tener entre 5 y 13 caracteres")
    @Pattern(regexp = "[A-Za-z0-9 ]+", message = "El RFC debe ser alfanumérico")
    private String rfc;

    @Column(nullable = false)
    @NotEmpty(message = "La razón social no puede estar vacío")
    @NotNull(message = "La razón social no puede ser nulo")
    @Size(min = 5, max = 20, message = "La razón social debe tener entre 5 y 15 caracteres")
    @Pattern(regexp = "[A-Za-z ]+", message = "La razón social solo debe de contener letras")
    private String businessName;


    @Column(nullable = false)
    @NotEmpty(message = "El número de celular no puede estar vacío")
    @NotNull(message = "El número de celular no puede ser nulo")
    @Size(min = 10, max = 13, message = "El número debe de contener entre 10 y 13 caracteres")
    @Pattern(regexp = "[0-9 ]+", message = "El número debe ser numérico")
    private String phone;

    @Column(nullable = false, unique = true)
    @NotEmpty(message = "El correo no puede estar vacía")
    @NotNull(message = "El correo no puede ser nulo")
    @Email(message = "El correo debe ser válido")
    @Size(min = 8, max = 35, message = "El correo debe tener entre 8 y 35 caracteres")
    private String email;

    @Column(nullable = false)
    @Min(value = 0, message = "El status debe ser 0 o 1")
    @Max(value = 1, message = "El status debe ser 0 o 1")
    private Integer status;

    @Override
    public String toString() {
        return "SupplierModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", rfc='" + rfc + '\'' +
                ", businessName=" + businessName + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", status=" + status +
                '}';
    }
}
