package mx.edu.utez.warehouse.warehouse.service;

import mx.edu.utez.warehouse.supplier.model.SupplierModel;
import mx.edu.utez.warehouse.warehouse.model.WarehouseModel;

import java.util.List;

public interface WarehouseService {
    List<WarehouseModel> findWarehouses();
}
