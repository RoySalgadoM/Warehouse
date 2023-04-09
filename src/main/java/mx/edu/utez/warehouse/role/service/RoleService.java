package mx.edu.utez.warehouse.role.service;


import mx.edu.utez.warehouse.role.model.RoleModel;


import java.util.List;


public interface RoleService {
    List<RoleModel> findRoles();


    List<RoleModel> findRolesByIds(List<Long> authorities);




}

