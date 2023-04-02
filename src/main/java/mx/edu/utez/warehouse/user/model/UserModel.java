package mx.edu.utez.warehouse.user.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.warehouse.person.model.PersonModel;
import mx.edu.utez.warehouse.role.service.model.RoleModel;

import java.util.Date;
import java.util.Set;


@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "user")
public class UserModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private Integer status;

    @Column
    private Date lastAccess;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<RoleModel> authorities;

    @OneToOne
    @JoinColumn(name = "person_id", nullable = false)
    private PersonModel person;


    public UserModel(Long id, String username, String password, Integer status, Set<RoleModel> authorities, PersonModel person, Date lastAccess) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.status = status;
        this.authorities = authorities;
        this.person = person;
        this.lastAccess = lastAccess;
    }
    public UserModel(String username, String password, Integer status, Set<RoleModel> authorities, PersonModel person, Date lastAccess) {
        this.username = username;
        this.password = password;
        this.status = status;
        this.authorities = authorities;
        this.person = person;
        this.lastAccess = lastAccess;
    }

}
