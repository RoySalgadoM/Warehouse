package mx.edu.utez.warehouse.area.model;

import jakarta.persistence.*;
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
    private long id;

    @Column(nullable = false, unique = true)
    private String identifier;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private int status;

}
