package mx.edu.utez.warehouse.area.service;

import mx.edu.utez.warehouse.area.model.AreaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AreaRepository extends JpaRepository<AreaModel, Long> {
    boolean existsByIdentifier(String identifier);
    boolean existsByIdentifierAndIdNotLike(String identifier, long id);

}
