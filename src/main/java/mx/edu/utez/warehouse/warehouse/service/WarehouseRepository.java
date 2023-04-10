package mx.edu.utez.warehouse.warehouse.service;

import mx.edu.utez.warehouse.warehouse.model.WarehouseModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface WarehouseRepository extends JpaRepository<WarehouseModel, Long> {
    boolean existsByIdentifier(String identifier);
    boolean existsByIdentifierAndIdNotLike(String identifier, long id);

}
