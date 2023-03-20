package mx.edu.utez.warehouse.security.service;

import mx.edu.utez.warehouse.WarehouseApplication;
import mx.edu.utez.warehouse.security.config.SecurityUser;
import mx.edu.utez.warehouse.user.model.UserModel;
import mx.edu.utez.warehouse.user.service.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class SecurityUserDetailsService implements UserDetailsService{

    @Autowired
    private UserRepository userRepository;
    private static final Logger logger = LogManager.getLogger(WarehouseApplication.class);

    public SecurityUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UUID uuid = UUID.randomUUID();
        try {

            logger.info("[USER : " + username + "] || [UUID : " + uuid.toString() + "] ---> EXECUTING LOGIN MODULE");

            var user = userRepository.findByUsername(username);
            if (user.isPresent()) {
                logger.info("[USER : " + username + "] || [UUID : " + uuid.toString() + "] ---> LOGIN SUCCESSFULLY");
                user.get().setLastAccess(new Date());
                userRepository.saveAndFlush(user.get());
                return new SecurityUser(user.get());
            }

            throw new UsernameNotFoundException("User not found");

        }catch (UsernameNotFoundException exception){
            logger.info("[USER : " + username + "] || [UUID : " + uuid.toString() + "] ---> USER DOES NOT EXIST");
            throw new UsernameNotFoundException("User not found");
        }catch (Exception exception){
            logger.info("[USER : " + username + "] || [UUID : " + uuid.toString() + "] ---> ERROR" + exception.getMessage());
            throw new RuntimeException("Error while trying to find user");
        }
    }


}
