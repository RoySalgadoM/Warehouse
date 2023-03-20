package mx.edu.utez.warehouse.security.config;

import lombok.AllArgsConstructor;
import mx.edu.utez.warehouse.role.model.RoleModel;
import org.springframework.security.core.GrantedAuthority;

@AllArgsConstructor
public class SecurityAuthority implements GrantedAuthority {

    private final RoleModel authority;

    @Override
    public String getAuthority() {
        return authority.getName().toString();
    }
}
