package mx.edu.utez.warehouse.role.service;


import mx.edu.utez.warehouse.role.model.RoleModel;


import java.util.List;
import java.util.Set;


public interface RoleService {
    List<RoleModel> findRoles();


    List<RoleModel> findRolesByIds(List<Long> authorities);




}

