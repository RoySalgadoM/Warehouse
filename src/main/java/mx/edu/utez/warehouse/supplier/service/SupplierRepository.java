package mx.edu.utez.warehouse.supplier.service;

import mx.edu.utez.warehouse.supplier.model.SupplierModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierRepository extends JpaRepository<SupplierModel, Long> {
}
