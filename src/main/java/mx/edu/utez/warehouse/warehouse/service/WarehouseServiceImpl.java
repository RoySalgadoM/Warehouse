package mx.edu.utez.warehouse.warehouse.service;

import mx.edu.utez.warehouse.warehouse.model.WarehouseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WarehouseServiceImpl implements WarehouseService{
    @Autowired
    WarehouseRepository repository;

    @Override
    public List<WarehouseModel> findWarehouses() {
        return repository.findAll();
    }
}
