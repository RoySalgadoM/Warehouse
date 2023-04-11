package mx.edu.utez.warehouse.entry.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mx.edu.utez.warehouse.requisition.model.RequisitionModel;
import mx.edu.utez.warehouse.supplier.model.SupplierModel;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "entry")
public class EntryModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Positive(message = "El id debe ser positivo")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = false)
    private SupplierModel supplier;

    @OneToOne
    @JoinColumn(name = "requisition_id", nullable = false)
    private RequisitionModel requisition;

    @Override
    public String toString() {
        return "EntryModel{" +
                "id=" + id +
                ", supplier=" + supplier +
                ", requisition=" + requisition +
                '}';
    }
}
