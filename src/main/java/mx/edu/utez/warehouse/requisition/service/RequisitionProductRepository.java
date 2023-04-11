package mx.edu.utez.warehouse.requisition.service;

import mx.edu.utez.warehouse.requisition.model.RequisitionProductModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequisitionProductRepository extends JpaRepository<RequisitionProductModel, Long> {
}
