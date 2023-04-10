package mx.edu.utez.warehouse.warehouse.service;

import mx.edu.utez.warehouse.message.model.MessageModel;
import mx.edu.utez.warehouse.warehouse.model.WarehouseModel;
import org.springframework.data.domain.Pageable;

public interface WarehouseService {
    MessageModel findAllWarehouse(Pageable page, String username, String uuid);
    MessageModel findById(long id, String username, String uuid);
    MessageModel registerWarehouse(WarehouseModel warehouseModel, String username, String uuid);
    MessageModel updateWarehouse(WarehouseModel warehouseModel, String username, String uuid);


}
