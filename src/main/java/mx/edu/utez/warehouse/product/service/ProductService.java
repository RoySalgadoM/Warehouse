package mx.edu.utez.warehouse.product.service;

import mx.edu.utez.warehouse.message.model.MessageModel;
import mx.edu.utez.warehouse.product.model.ProductModel;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    List<ProductModel> findProductsByType(Integer type);
    MessageModel findAllProducts(Pageable page, String username, String uuid);
    MessageModel findById(long id, String username, String uuid);
    MessageModel registerProduct(ProductModel productModel, String username, String uuid);
    MessageModel updateProduct(ProductModel productModel, String username, String uuid);
    MessageModel disableProduct(long id, String username, String uuid);
}
