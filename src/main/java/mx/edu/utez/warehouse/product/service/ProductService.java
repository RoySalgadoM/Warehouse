package mx.edu.utez.warehouse.product.service;

import mx.edu.utez.warehouse.product.model.ProductModel;

import java.util.List;

public interface ProductService {
    List<ProductModel> findProductsByType(Integer type);
}
