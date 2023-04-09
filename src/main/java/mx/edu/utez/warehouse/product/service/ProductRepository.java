package mx.edu.utez.warehouse.product.service;

import mx.edu.utez.warehouse.product.model.ProductModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<ProductModel, Long> {
    List<ProductModel> findProductsByTypeAndAndStatus(Integer type, Integer status );
}
