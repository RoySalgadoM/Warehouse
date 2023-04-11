package mx.edu.utez.warehouse.warehouse.service;

import mx.edu.utez.warehouse.product.model.WarehouseProductModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DashboardRepository extends JpaRepository<WarehouseProductModel, Long> {

}