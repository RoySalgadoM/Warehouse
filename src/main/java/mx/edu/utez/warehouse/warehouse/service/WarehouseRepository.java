package mx.edu.utez.warehouse.warehouse.service;

import mx.edu.utez.warehouse.warehouse.model.WarehouseModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarehouseRepository extends JpaRepository<WarehouseModel, Long> {
}
