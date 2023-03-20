package mx.edu.utez.warehouse.role.model;

import jakarta.persistence.*;

@Entity
@Table(name = "role")
public class RoleModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;


}
