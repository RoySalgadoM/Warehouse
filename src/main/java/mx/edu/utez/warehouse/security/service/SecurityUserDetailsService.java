package mx.edu.utez.warehouse.security.service;

import jakarta.persistence.NoResultException;
import mx.edu.utez.warehouse.security.config.SecurityUser;
import mx.edu.utez.warehouse.user.service.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SecurityUserDetailsService implements UserDetailsService{

    @Autowired
    private UserRepository userRepository;
    private static final Logger logger = LogManager.getLogger(SecurityUserDetailsService.class);

    public SecurityUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UUID uuid = UUID.randomUUID();
        try {

            logger.info("[USER : {}] || [UUID : {}] ---> EXECUTING LOGIN MODULE", username, uuid);

            var user = userRepository.findByUsername(username);
            if (user.isPresent()) {
                return new SecurityUser(user.get());
            }

            throw new UsernameNotFoundException("USER DOES NOT EXIST");

        }catch (Exception exception){
            logger.error("[USER : {}] || [UUID : {}] ---> ERROR ", username, exception.getMessage());
            throw new NoResultException("Error while trying to find user");
        }
    }


}
