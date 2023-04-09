package mx.edu.utez.warehouse.product.service;

import mx.edu.utez.warehouse.product.model.ProductModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService{
    @Autowired
    ProductRepository repository;

    @Override
    public List<ProductModel> findProductsByType(Integer type) {
        List<ProductModel> products = repository.findAll();
        return products;
    }
}
