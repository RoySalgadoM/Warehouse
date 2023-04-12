package mx.edu.utez.warehouse.warehouse.service;

import mx.edu.utez.warehouse.user.model.UserModel;
import mx.edu.utez.warehouse.warehouse.model.WarehouseModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface WarehouseRepository extends JpaRepository<WarehouseModel, Long> {
    boolean existsByIdentifier(String identifier);
    boolean existsByIdentifierAndIdNotLike(String identifier, Long id);
    boolean existsById(Long id);

    Page<WarehouseModel> findAllByWarehouser(UserModel userModel, Pageable page);
    Page<WarehouseModel> findAllByInvoicer(UserModel userModel, Pageable page);
}
