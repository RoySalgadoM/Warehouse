package mx.edu.utez.warehouse.log.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "log")
public class LogModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    private String uuid;
    @Column(nullable = false)
    private String action;
    @Column(nullable = false)
    private Long idAction;
    @Column(nullable = false)
    private Date date;

    public LogModel(String username, String uuid, String action, Long idAction, Date date) {
        this.username = username;
        this.uuid = uuid;
        this.action = action;
        this.idAction = idAction;
        this.date = date;
    }
}
