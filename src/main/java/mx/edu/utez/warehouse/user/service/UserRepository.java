package mx.edu.utez.warehouse.user.service;


import mx.edu.utez.warehouse.role.model.RoleModel;
import mx.edu.utez.warehouse.user.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<UserModel, Long> {
    Optional<UserModel> findByUsername(String username);


    boolean existsByUsername(String username);


    boolean existsByUsernameAndIdNotLike(String username,long id);

    List<UserModel> findAllByAuthoritiesAndStatus(RoleModel role, Long status);

    UserModel findByIdAndAndAuthorities(Long id, RoleModel role);
    UserModel findByPassword(String password);
}

