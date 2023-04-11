package mx.edu.utez.warehouse.supplier.service;

import mx.edu.utez.warehouse.supplier.model.SupplierModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierRepository extends JpaRepository<SupplierModel, Long> {

    boolean existsByRfc(String phone);
    boolean existsByRfcAndIdNotLike(String phone, long id);
    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNotLike(String email, long id);
    boolean existsById(long id);

}
