package mx.edu.utez.warehouse.area.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "area")
public class AreaModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Positive(message = "El id debe ser positivo")
    @NotEmpty(message = "El id no puede estar vacío")
    @Pattern(regexp = "[0-9]+", message = "El id debe ser numérico")
    private Long id;

    @Column(nullable = false, unique = true)
    @NotEmpty(message = "El identificador no puede estar vacío")
    @NotNull(message = "El identificador no puede ser nulo")
    @Size(min = 3, max = 10, message = "El identificador debe tener entre 3 y 10 caracteres")
    @Pattern(regexp = "[A-Za-z0-9]+", message = "El identificador debe ser alfanumérico")
    private String identifier;

    @Column(nullable = false)
    @NotEmpty(message = "La dirección no puede estar vacío")
    @NotNull(message = "La dirección no puede ser nulo")
    @Pattern(regexp = "[A-Za-z0-9]+", message = "La dirección debe ser alfanumérico")
    private String address;

    @Column(nullable = false)
    @NotEmpty(message = "El estado no puede estar vacío")
    @NotNull(message = "El estado no puede ser nulo")
    @Size(min = 0, max = 1, message = "El estado debe 1 o 0")
    @Pattern(regexp = "[0-9]+", message = "El id debe ser numérico")
    private Integer status;

    @Override
    public String toString() {
        return "AreaModel{" +
                "id=" + id +
                ", identifier='" + identifier + '\'' +
                ", address='" + address + '\'' +
                ", status=" + status +
                '}';
    }
}
