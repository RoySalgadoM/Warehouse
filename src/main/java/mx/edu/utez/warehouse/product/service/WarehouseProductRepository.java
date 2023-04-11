package mx.edu.utez.warehouse.product.service;

import mx.edu.utez.warehouse.product.model.ProductModel;
import mx.edu.utez.warehouse.product.model.WarehouseProductModel;
import mx.edu.utez.warehouse.warehouse.model.WarehouseModel;
import mx.edu.utez.warehouse.warehouse.service.WarehouseRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WarehouseProductRepository extends JpaRepository<WarehouseProductModel, Long> {
    WarehouseProductModel findWarehouseProductByWarehouseAndProduct(WarehouseModel warehouse, ProductModel product);
}
