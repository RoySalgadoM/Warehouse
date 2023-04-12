package mx.edu.utez.warehouse.requisition.service;

import mx.edu.utez.warehouse.requisition.model.RequisitionModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequisitionRepository extends JpaRepository<RequisitionModel, Long> {
    boolean existsByCode(String code);
}
