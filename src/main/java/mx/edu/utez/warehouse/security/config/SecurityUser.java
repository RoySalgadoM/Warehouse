package mx.edu.utez.warehouse.security.config;


import lombok.AllArgsConstructor;
import mx.edu.utez.warehouse.security.config.SecurityAuthority;
import mx.edu.utez.warehouse.user.model.UserModel;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@AllArgsConstructor
public class SecurityUser implements UserDetails {
    private final UserModel user;
    @Override
    public String getUsername() {
        return user.getUsername();
    }
    @Override
    public String getPassword() {
        return user.getPassword();
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getAuthorities().stream().map(SecurityAuthority::new).toList();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
