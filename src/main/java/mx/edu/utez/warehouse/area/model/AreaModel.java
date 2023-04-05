package mx.edu.utez.warehouse.area.model;

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
@Table(name = "area")
public class AreaModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Positive(message = "El id debe ser positivo")
    private Long id;

    @Column(nullable = false, unique = true)
    @NotEmpty(message = "El identificador no puede estar vacío")
    @NotNull(message = "El identificador no puede ser nulo")
    @Size(min = 3, max = 10, message = "El identificador debe tener entre 3 y 10 caracteres")
    @Pattern(regexp = "[A-Za-z0-9 ]+", message = "El identificador debe ser alfanumérico")
    private String identifier;

    @Column(nullable = false)
    @NotEmpty(message = "La dirección no puede estar vacío")
    @NotNull(message = "La dirección no puede ser nulo")
    @Size(min = 3, max = 45, message = "La dirección debe tener entre 3 y 45 caracteres")
    @Pattern(regexp = "[A-Za-z0-9 ]+", message = "La dirección debe ser alfanumérico")
    private String address;

    @Column(nullable = true, columnDefinition = "int default 1")
    @Positive(message = "El estado debe ser positivo")
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
