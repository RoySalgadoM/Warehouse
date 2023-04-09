package mx.edu.utez.warehouse.user.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.warehouse.role.model.RoleModel;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
    private String password;


    @Column(nullable = false)
    @Min(value = 0, message = "El status debe ser 0 o 1")
    @Max(value = 1, message = "El status debe ser 0 o 1")
    private Integer status;


    @Column(nullable = false, unique = true)
    @NotEmpty(message = "El correo no puede estar vacía")
    @NotNull(message = "El correo no puede ser nulo")
    @Email(message = "El correo debe ser válido")
    @Size(min = 8, max = 35, message = "El correo debe tener entre 8 y 35 caracteres")
    private String email;

    @Column(nullable = false)
    @NotEmpty(message = "El nombre no puede estar vacío")
    @NotNull(message = "El nombre no puede ser nulo")
    @Size(min = 3, max = 10, message = "El nombre debe tener entre 3 y 10 caracteres")
    @Pattern(regexp = "[A-Za-z0-9 ]+", message = "El nombre debe ser alfanumérico")
    private String name;

    @Column(nullable = false)
    @NotEmpty(message = "El apellido no puede estar vacío")
    @NotNull(message = "El apellido no puede ser nulo")
    @Size(min = 3, max = 10, message = "El apellido debe tener entre 3 y 10 caracteres")
    @Pattern(regexp = "[A-Za-z0-9 ]+", message = "El apellido debe ser alfanumérico")
    private String surname;


    @Column(nullable = false)
    @NotEmpty(message = "El apellido no puede estar vacío")
    @NotNull(message = "El apellido no puede ser nulo")
    @Size(min = 3, max = 10, message = "El apellido debe tener entre 3 y 10 caracteres")
    @Pattern(regexp = "[A-Za-z0-9 ]+", message = "El apellido debe ser alfanumérico")
    private String secondSurname;


    @Column(nullable = false)
    @NotEmpty(message = "El teléfono no puede estar vacío")
    @Size(min = 10, max = 13, message = "El teléfono debe tener entre 10 y 14 caracteres")
    @Pattern(regexp = "[0-9 ]+", message = "El teléfono debe ser válido")

    private String phone;


    @Column(nullable = false)
    @NotEmpty(message = "El RFC no puede estar vacío")
    @NotNull(message = "El RFC no puede ser nulo")
    @Size(min = 9, max = 13, message = "El RFC debe tener entre 9 y 13 caracteres")
    @Pattern(regexp = "[A-Za-z0-9 ]+", message = "El RFC debe ser alfanumérico")
    private String rfc;


    @Column
    private Date lastAccess;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "role_id"))
    private Set<RoleModel> authorities;


    public UserModel(Long id, String username, String password, Integer status, Set<RoleModel> authorities, Date lastAccess, String email, String name, String surname,
                     String secondSurname, String phone, String rfc) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.status = status;
        this.authorities = authorities;
        this.lastAccess = lastAccess;
        this.email = email;
        this.name=name;
        this.surname=surname;
        this.secondSurname=secondSurname;
        this.phone=phone;
        this.rfc=rfc;
    }


    public String toString(){
        return "UserModel{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", authorities='" + authorities + '\'' +
                ", status=" + status +
                '}';
    }


}


