package mx.edu.utez.warehouse.supplier.service;

import mx.edu.utez.warehouse.message.model.MessageModel;
import mx.edu.utez.warehouse.supplier.model.SupplierModel;
import org.springframework.data.domain.Pageable;

public interface SupplierService {
    MessageModel findAllSupplies(Pageable page, String username, String uuid);
    MessageModel findById(long id, String username, String uuid);
    MessageModel registerSupplier(SupplierModel supplierModel, String username, String uuid);
    MessageModel updateSupplier(SupplierModel supplierModel, String username, String uuid);
    MessageModel disableSupplier(long id, String username, String uuid);
}
