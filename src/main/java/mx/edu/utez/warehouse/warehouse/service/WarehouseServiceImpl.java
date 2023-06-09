package mx.edu.utez.warehouse.warehouse.service;

import jakarta.persistence.NoResultException;
import mx.edu.utez.warehouse.message.model.MessageModel;

import mx.edu.utez.warehouse.product.model.ProductModel;
import mx.edu.utez.warehouse.product.model.WarehouseProductModel;
import mx.edu.utez.warehouse.user.model.UserModel;
import mx.edu.utez.warehouse.utils.MessageCatalog;
import mx.edu.utez.warehouse.warehouse.model.WarehouseDTO;
import mx.edu.utez.warehouse.warehouse.model.WarehouseModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class WarehouseServiceImpl implements WarehouseService {
    private static final Logger logger = LogManager.getLogger(WarehouseServiceImpl.class);
    private static final String WAREHOUSE_NOT_FOUND = "The warehouse could not be found";

    @Autowired
    WarehouseRepository repository;

    @Override
    public MessageModel findAllWarehouse(Pageable page, String username, String uuid) {
        try {
            Page<WarehouseModel> warehouses = repository.findAll(page);

            if (warehouses.getNumberOfElements() == 0) {
                return new MessageModel(MessageCatalog.NO_RECORDS_FOUND, null, false);
            }

            return new MessageModel(MessageCatalog.RECORDS_FOUND, warehouses, false);

        } catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> WAREHOUSE MODULE ---> findAllWarehouse() ERROR: {}", username, uuid, exception.getMessage());
            return new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
        }
    }

    @Override
    public List<WarehouseModel> findWarehousesByWarehouser(String username) {
        List<WarehouseModel> warehouses = repository.findAll();
        warehouses = warehouses.stream().filter(warehouse -> warehouse.getWarehouser().getUsername().equals(username) && warehouse.getWarehouser().getAuthorities().stream().anyMatch(roleModel -> roleModel.getId() == 2)).toList();
        return warehouses;
    }

    @Override
    public List<WarehouseModel> findWarehousesByInvoicer(String username) {
        List<WarehouseModel> warehouses = repository.findAll();
        warehouses = warehouses.stream().filter(warehouse -> warehouse.getInvoicer().getUsername().equals(username) && warehouse.getInvoicer().getAuthorities().stream().anyMatch(roleModel -> roleModel.getId() == 3)).toList();
        return warehouses;
    }
    @Override
    public MessageModel findWarehousesByWarehouserW(Pageable page, UserModel userModel, String uuid) {
        try {
            Page<WarehouseModel> warehouses = repository.findAllByWarehouser(userModel, page);

            if (warehouses.getNumberOfElements() == 0) {
                return new MessageModel(MessageCatalog.NO_RECORDS_FOUND, null, false);
            }

            return new MessageModel(MessageCatalog.RECORDS_FOUND, warehouses, false);

        } catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> WAREHOUSE MODULE ---> findAllWarehouse() ERROR: {}", userModel.getUsername(), uuid, exception.getMessage());
            return new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
        }
    }

    @Override
    public MessageModel findWarehousesTotal(Pageable page, String username, String uuid) {
        try {
            Page<WarehouseModel> warehouses = repository.findAll(page);
            if(warehouses.getNumberOfElements() == 0){
                return new MessageModel(MessageCatalog.NO_RECORDS_FOUND, null, false);
            }
            List<WarehouseDTO> listWarehouse = new ArrayList<>();
            for (WarehouseModel warehouse : warehouses) {
                Integer quantity = 0;
                Double amount = 0.0;
                List<WarehouseProductModel> listWarehouseProduct = warehouse.getWarehouseProductModels();
                for(WarehouseProductModel warehouseProduct : listWarehouseProduct){
                    quantity += warehouseProduct.getQuantity();
                    amount += warehouseProduct.getQuantity() * warehouseProduct.getProduct().getUnitPrice();
                }
                WarehouseDTO warehouseDTO = new WarehouseDTO(warehouse.getId(), warehouse.getName(), warehouse.getIdentifier(), quantity, amount);
                listWarehouse.add(warehouseDTO);
            }
            return new MessageModel(MessageCatalog.RECORDS_FOUND, listWarehouse, false);
        }catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> WAREHOUSE MODULE ---> findWarehousesTotal() ERROR: {}", username, uuid, exception.getMessage());
            return new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
        }
    }

    @Override
    public MessageModel findWarehousesByInvoicerW(Pageable page, UserModel userModel, String uuid) {
        try {
            Page<WarehouseModel> warehouses = repository.findAllByInvoicer(userModel, page);

            if (warehouses.getNumberOfElements() == 0) {
                return new MessageModel(MessageCatalog.NO_RECORDS_FOUND, null, false);
            }

            return new MessageModel(MessageCatalog.RECORDS_FOUND, warehouses, false);

        } catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> WAREHOUSE MODULE ---> findAllWarehouse() ERROR: {}", userModel.getUsername(), uuid, exception.getMessage());
            return new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
        }
    }

    @Override
    public List<WarehouseModel> findWarehouses() {
        return repository.findAll();
    }

    @Override
    public List<ProductModel> findWarehouseProductsByType(Integer type, long idWarehouse) {
        var warehouse = repository.findById(idWarehouse);
        if (warehouse.isEmpty()) {
            throw new NoResultException(WAREHOUSE_NOT_FOUND);
        }
        List<WarehouseProductModel> warehouseProducts = warehouse.get().getWarehouseProductModels();
        List<ProductModel> products = warehouseProducts.stream().map(WarehouseProductModel::getProduct).toList();
        products = products.stream().filter(product -> product.getType().equals(type)).toList();
        return products;
    }

    @Override
    public MessageModel findById(long id, String username, String uuid) {
        try {
            var findWarehouse = repository.findById(id);
            if (findWarehouse.isEmpty()) {
                throw new NoResultException(WAREHOUSE_NOT_FOUND);
            }
            return new MessageModel(MessageCatalog.RECORDS_FOUND, findWarehouse.get(), false);

        } catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> WAREHOUSE MODULE SERVICEIMPL ---> findById() ERROR: {}", username, uuid, exception.getMessage());
            return new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
        }
    }

    @Override
    public MessageModel registerWarehouse(WarehouseModel warehouseModel, String username, String uuid) {
        MessageModel messageModel;
        try {

            repository.save(warehouseModel);
            messageModel = new MessageModel(MessageCatalog.SUCCESS_REGISTER, null, false);

        } catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> WAREHOUSE MODULE ---> saveWarehouse() ERROR: {}", username, uuid, exception.getMessage());
            messageModel = new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
        }
        return messageModel;
    }

    @Override
    public MessageModel updateWarehouse(WarehouseModel warehouseModel, String username, String uuid) {
        try {
            var warehouse = repository.findById(warehouseModel.getId());
            if (warehouse.isEmpty()) {
                throw new NoResultException(WAREHOUSE_NOT_FOUND);
            }

            warehouse.get().setName(warehouseModel.getName());
            warehouse.get().setIdentifier(warehouseModel.getIdentifier());
            warehouse.get().setAddress(warehouseModel.getAddress());
            warehouse.get().setWarehouser(warehouseModel.getWarehouser());
            warehouse.get().setInvoicer(warehouseModel.getInvoicer());

            repository.saveAndFlush(warehouse.get());

            return new MessageModel(MessageCatalog.SUCCESS_UPDATE, null, false);

        } catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> WAREHOUSE MODULE ---> updateWarehouse() ERROR: {}", username, uuid, exception.getMessage());
            return new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
        }
    }

    @Override
    public MessageModel findAllCost(Pageable page, String username, String uuid) {
        return null;
    }

    public boolean isExistWarehouse(String identifier) {
        return repository.existsByIdentifier(identifier);
    }
    public boolean isExistWarehouseAndId(String identifier, Long id) {
        return repository.existsByIdentifierAndIdNotLike(identifier, id);
    }

    public boolean isExistById(Long id) {
        return repository.existsById(id);
    }

}
