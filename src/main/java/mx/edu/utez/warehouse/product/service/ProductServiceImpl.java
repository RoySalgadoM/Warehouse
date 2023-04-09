package mx.edu.utez.warehouse.product.service;

import jakarta.persistence.NoResultException;
import mx.edu.utez.warehouse.area.model.AreaModel;
import mx.edu.utez.warehouse.message.model.MessageModel;
import mx.edu.utez.warehouse.product.model.ProductModel;
import mx.edu.utez.warehouse.utils.MessageCatalog;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService{

    private static final Logger logger = LogManager.getLogger(ProductServiceImpl.class);
    @Autowired
    ProductRepository repository;

    @Override
    public MessageModel findAllProducts(Pageable page, String username, String uuid) {
        try {
            Page<ProductModel> products = repository.findAll(page);

            if (products.getNumberOfElements() == 0) {
                return new MessageModel(MessageCatalog.NO_RECORDS_FOUND, null, false);
            }

            return new MessageModel(MessageCatalog.RECORDS_FOUND, products, false);

        } catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> PRODUCT MODULE ---> findAllProducts() ERROR: {}", username, uuid, exception.getMessage());
            return new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
        }
    }

    @Override
    public MessageModel findById(long id, String username, String uuid) {
        try {
            var findProduct = repository.findById(id);
            if (findProduct.isEmpty()) {
                throw new NoResultException("The product could not be found");
            }
            return new MessageModel(MessageCatalog.RECORDS_FOUND, findProduct.get(), false);

        } catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> PRODUCT MODULE ---> findById() ERROR: {}", username, uuid, exception.getMessage());
            return new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
        }
    }

    @Override
    public MessageModel registerProduct(ProductModel productModel, String username, String uuid) {
        MessageModel messageModel;
        try {
            productModel.setStatus(1);
            repository.save(productModel);
            messageModel = new MessageModel(MessageCatalog.SUCCESS_REGISTER, null, false);

        } catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> PRODUCT MODULE ---> saveProduct() ERROR: {}", username, uuid, exception.getMessage());
            messageModel = new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
        }
        return messageModel;
    }

    @Override
    public MessageModel updateProduct(ProductModel productModel, String username, String uuid) {
        try {
            var product = repository.findById(productModel.getId());
            if (product.isEmpty()) {
                throw new NoResultException("The product could not be found");
            }
            product.get().setDescription(productModel.getDescription());
            product.get().setName(productModel.getName());
            product.get().setUnit(productModel.getUnit());
            product.get().setUnitPrice(productModel.getUnitPrice());
            product.get().setExpirationDate(productModel.getExpirationDate());
            product.get().setSerialNumber(productModel.getSerialNumber());
            product.get().setType(productModel.getType());

            repository.saveAndFlush(product.get());
            return new MessageModel(MessageCatalog.SUCCESS_UPDATE, null, false);

        } catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> PRODUCT MODULE ---> updateProduct() ERROR: {}", username, uuid, exception.getMessage());
            return new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
        }
    }

    @Override
    public MessageModel disableProduct(long id, String username, String uuid) {
        try {
            var product = repository.findById(id);
            if (product.isEmpty()) {
                throw new NoResultException("The area could not be found");
            }
            product.get().setStatus(product.get().getStatus() == 1 ? 0 : 1);
            repository.saveAndFlush(product.get());
            return new MessageModel(product.get().getStatus() == 1 ? MessageCatalog.SUCCESS_ENABLE : MessageCatalog.SUCCESS_DISABLE, null, false);

        } catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> PRODUCT MODULE ---> disableProduct() ERROR: {}", username, uuid, exception.getMessage());
            return new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
        }
    }

    @Override
    public List<ProductModel> findProductsByType(Integer type) {
        List<ProductModel> products = repository.findAll();
        return products;
    }

}
