package mx.edu.utez.warehouse.role.service;


import mx.edu.utez.warehouse.role.model.RoleModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;


@Repository
public interface RoleRepository extends JpaRepository<RoleModel, Long> {


    List<RoleModel> findByIdIn(List<Long> authoritiesIds);

}