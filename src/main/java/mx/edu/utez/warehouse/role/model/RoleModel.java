package mx.edu.utez.warehouse.role.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(name = "role")
@Getter
@Setter
@NoArgsConstructor
public class RoleModel implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private AuthorityName name;


    public RoleModel(Long id, AuthorityName name) {
        this.id = id;
        this.name = name;
    }
    public RoleModel(Long id) {
        this.id = id;
    }
}
