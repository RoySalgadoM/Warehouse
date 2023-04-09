package mx.edu.utez.warehouse.product.service;

import mx.edu.utez.warehouse.product.model.ProductModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<ProductModel, Long> {
}
