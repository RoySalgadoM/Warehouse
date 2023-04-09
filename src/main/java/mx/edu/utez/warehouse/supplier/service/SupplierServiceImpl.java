package mx.edu.utez.warehouse.supplier.service;

import mx.edu.utez.warehouse.supplier.model.SupplierModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SupplierServiceImpl implements SupplierService{
    @Autowired
    SupplierRepository repository;

    @Override
    public List<SupplierModel> findSupplies() {
        return repository.findAll();
    }
}
