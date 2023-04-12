package mx.edu.utez.warehouse.supplier.service;

import jakarta.persistence.NoResultException;
import mx.edu.utez.warehouse.supplier.model.SupplierModel;
import mx.edu.utez.warehouse.message.model.MessageModel;
import mx.edu.utez.warehouse.utils.MessageCatalog;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SupplierServiceImpl implements SupplierService{
    private static final Logger logger = LogManager.getLogger(SupplierServiceImpl.class);
    private static final String SUPPLIER_NOT_FOUND = "The supplier could not be found";

    @Autowired
    SupplierRepository repository;

    @Override
    public List<SupplierModel> findSupplies() {
        return repository.findAll();
    }
    
    @Override
    public MessageModel findAllSupplies(Pageable page, String username, String uuid) {
        try {
            Page<SupplierModel> supplies = repository.findAll(page);

            if (supplies.getNumberOfElements() == 0) {
                return new MessageModel(MessageCatalog.NO_RECORDS_FOUND, null, false);
            }

            return new MessageModel(MessageCatalog.RECORDS_FOUND, supplies, false);

        } catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> SUPPLIER MODULE ---> findAllSupplies() ERROR: {}", username, uuid, exception.getMessage());
            return new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
        }
    }

    @Override
    public MessageModel findById(long id, String username, String uuid) {
        try {
            var findSupplier = repository.findById(id);
            if (findSupplier.isEmpty()) {
                throw new NoResultException(SUPPLIER_NOT_FOUND);
            }
            return new MessageModel(MessageCatalog.RECORDS_FOUND, findSupplier.get(), false);

        } catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> SUPPLIER MODULE ---> findById() ERROR: {}", username, uuid, exception.getMessage());
            return new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
        }
    }

    @Override
    public MessageModel registerSupplier(SupplierModel supplierModel, String username, String uuid) {
        MessageModel messageModel;
        try {
            supplierModel.setStatus(1);
            repository.save(supplierModel);
            messageModel = new MessageModel(MessageCatalog.SUCCESS_REGISTER, null, false);

        } catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> SUPPLIER MODULE ---> saveSupplier() ERROR: {}", username, uuid, exception.getMessage());
            messageModel = new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
        }
        return messageModel;
    }

    @Override
    public MessageModel updateSupplier(SupplierModel supplierModel, String username, String uuid) {
        try {
            var supplier = repository.findById(supplierModel.getId());
            if (supplier.isEmpty()) {
                throw new NoResultException(SUPPLIER_NOT_FOUND);
            }
            supplier.get().setName(supplierModel.getName());
            supplier.get().setRfc(supplierModel.getRfc());
            supplier.get().setBusinessName(supplierModel.getBusinessName());
            supplier.get().setPhone(supplierModel.getPhone());
            supplier.get().setEmail(supplierModel.getEmail());
            repository.saveAndFlush(supplier.get());
            return new MessageModel(MessageCatalog.SUCCESS_UPDATE, null, false);

        } catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> SUPPLIER MODULE ---> updateSupplier() ERROR: {}", username, uuid, exception.getMessage());
            return new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
        }
    }

    @Override
    public MessageModel disableSupplier(long id, String username, String uuid) {
        try {
            var supplier = repository.findById(id);
            if (supplier.isEmpty()) {
                throw new NoResultException(SUPPLIER_NOT_FOUND);
            }
            supplier.get().setStatus(supplier.get().getStatus() == 1 ? 0 : 1);
            repository.saveAndFlush(supplier.get());
            return new MessageModel(supplier.get().getStatus() == 1 ? MessageCatalog.SUCCESS_ENABLE : MessageCatalog.SUCCESS_DISABLE, null, false);

        } catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> SUPPLIER MODULE ---> disableSupplier() ERROR: {}", username, uuid, exception.getMessage());
           return new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
        }
    }

    public boolean isExistSupplierByRfcAndId(String name, Long id) {
        return repository.existsByRfcAndIdNotLike(name, id);
    }
    public boolean isExistSupplierByRfc(String rfc) {
        return repository.existsByRfc(rfc);
    }
    public boolean isExistSupplierByEmail(String email) {
        return repository.existsByEmail(email);
    }
    public boolean isExistSupplierByEmailAndId(String email, Long id) {
        return repository.existsByEmailAndIdNotLike(email, id);
    }
    public boolean isExistById(Long id) {
        return repository.existsById(id);
    }
}
