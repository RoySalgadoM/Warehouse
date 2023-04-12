package mx.edu.utez.warehouse.warehouse.service;

import mx.edu.utez.warehouse.message.model.MessageModel;
import mx.edu.utez.warehouse.product.model.ProductModel;
import mx.edu.utez.warehouse.warehouse.model.WarehouseModel;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface WarehouseService {
    MessageModel findAllWarehouse(Pageable page, String username, String uuid);
    List<WarehouseModel> findWarehousesByWarehouser(String username);
    List<WarehouseModel> findWarehousesByInvoicer(String username);
    List<WarehouseModel> findWarehouses();
    List<ProductModel> findWarehouseProductsByType(Integer type, long idWarehouse);
    MessageModel findById(long id, String username, String uuid);
    MessageModel registerWarehouse(WarehouseModel warehouseModel, String username, String uuid);
    MessageModel updateWarehouse(WarehouseModel warehouseModel, String username, String uuid);
    MessageModel findAllCost(Pageable page, String username, String uuid);



}
