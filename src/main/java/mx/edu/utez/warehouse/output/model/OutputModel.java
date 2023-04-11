package mx.edu.utez.warehouse.output.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.warehouse.area.model.AreaModel;
import mx.edu.utez.warehouse.requisition.model.RequisitionModel;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "output")
public class OutputModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Positive(message = "El id debe ser positivo")
    private Long id;

    @OneToOne
    @JoinColumn(name = "area_id", nullable = false)
    private AreaModel area;

    @OneToOne
    @JoinColumn(name = "requisition_id", nullable = false)
    private RequisitionModel requisition;
}
