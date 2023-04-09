package mx.edu.utez.warehouse.supplier.service;

import mx.edu.utez.warehouse.supplier.model.SupplierModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierRepository extends JpaRepository<SupplierModel, Long> {
    boolean existsByName(String name);
    boolean existsByNameAndIdNotLike(String name, long id);
}
