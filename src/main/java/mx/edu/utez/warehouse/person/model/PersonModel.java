package mx.edu.utez.warehouse.person.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.warehouse.user.model.UserModel;

import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "person")
public class PersonModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String surname;
    @Column(nullable = false)
    private String secondSurname;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String phone;
    @Column(nullable = false)
    private String rfc;

    @Column(nullable = false)
    private int status;


    public PersonModel(long id, String name, String surname, String secondSurname, String email, String phone, String rfc, int status) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.secondSurname = secondSurname;
        this.email = email;
        this.phone = phone;
        this.rfc = rfc;
        this.status = status;
    }
    public PersonModel(String name, String surname, String secondSurname, String email, String phone, String rfc, int status) {
        this.name = name;
        this.surname = surname;
        this.secondSurname = secondSurname;
        this.email = email;
        this.phone = phone;
        this.rfc = rfc;
        this.status = status;
    }

}
