package mx.edu.utez.warehouse.user.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.warehouse.person.model.PersonModel;
import mx.edu.utez.warehouse.role.model.RoleModel;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "user")
public class UserModel implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    @NotEmpty(message = "El username no puede estar vacío")
    @NotNull(message = "El username no puede ser nulo")
    @Size(min = 3, max = 10, message = "El username debe tener entre 3 y 10 caracteres")
    @Pattern(regexp = "[A-Za-z0-9 ]+", message = "El username debe ser alfanumérico")
    private String username;

    @Column(nullable = false, length = 255)
    @NotEmpty(message = "La contraseña no puede estar vacía")
    @NotNull(message = "La contraseña no puede ser nula")
    @Size(min = 8, max = 80, message = "La contraseña debe tener entre 8 y 16 caracteres")
    private String password;
    @Column(nullable = false)
    @Min(value = 0, message = "El status debe ser 0 o 1")
    @Max(value = 1, message = "El status debe ser 0 o 1")
    private Integer status;

    @Column(nullable = false)
    @NotEmpty(message = "El correo no puede estar vacía")
    @NotNull(message = "El correo no puede ser nulo")
    @Email(message = "El correo debe ser válido")
    private String email;

    @Column
    private Date lastAccess;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<RoleModel> authorities;

    @OneToOne
    @JoinColumn(name = "person_id", nullable = false)
    private PersonModel person;

    public UserModel(Long id, String username, String password, Integer status, Set<RoleModel> authorities, PersonModel person, Date lastAccess, String email) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.status = status;
        this.authorities = authorities;
        this.person = person;
        this.lastAccess = lastAccess;
        this.email = email;
    }

}
