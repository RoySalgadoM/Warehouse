package mx.edu.utez.warehouse.warehouse.service;

import jakarta.persistence.NoResultException;
import mx.edu.utez.warehouse.message.model.MessageModel;

import mx.edu.utez.warehouse.product.model.WarehouseProductModel;
import mx.edu.utez.warehouse.utils.MessageCatalog;
import mx.edu.utez.warehouse.warehouse.model.WarehouseModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class WarehouseServiceImpl implements WarehouseService {
    private static final Logger logger = LogManager.getLogger(WarehouseServiceImpl.class);

    @Autowired
    WarehouseRepository repository;

    @Autowired
    DashboardRepository repositoryD;


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
    public List<WarehouseModel> findWarehouses() {
        return repository.findAll();
    }

    @Override
    public MessageModel findAllCost(Pageable page, String username, String uuid) {
        try {
            Page<WarehouseProductModel> cost = repositoryD.findAll(page);

            if (cost.getNumberOfElements() == 0) {
                return new MessageModel(MessageCatalog.NO_RECORDS_FOUND, null, false);
            }
            return new MessageModel(MessageCatalog.RECORDS_FOUND, cost, false);

        } catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> ENTRY MODULE ---> findAllEntries() ERROR: {}", username, uuid, exception.getMessage());
            return new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
        }
    }

    @Override
    public MessageModel findById(long id, String username, String uuid) {
        try {
            var findWarehouse = repository.findById(id);
            if (findWarehouse.isEmpty()) {
                throw new NoResultException("The warehouse could not be found");
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
                throw new NoResultException("The warehouse could not be found");
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
