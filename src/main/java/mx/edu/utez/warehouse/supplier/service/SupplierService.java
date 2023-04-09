package mx.edu.utez.warehouse.supplier.service;

import mx.edu.utez.warehouse.supplier.model.SupplierModel;

import java.util.List;

public interface SupplierService {
    List<SupplierModel> findSupplies();
}
