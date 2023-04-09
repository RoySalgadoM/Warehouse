package mx.edu.utez.warehouse.role.service;


import mx.edu.utez.warehouse.role.model.RoleModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    RoleRepository repository;


    @Override
    public List<RoleModel> findRoles() {
        return repository.findAll();
    }


    @Override
    public List<RoleModel> findRolesByIds(List<Long> authorities) {
        return repository.findByIdIn(authorities);
    }






}

