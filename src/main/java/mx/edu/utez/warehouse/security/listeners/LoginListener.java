package mx.edu.utez.warehouse.security.listeners;

import mx.edu.utez.warehouse.user.service.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class LoginListener implements ApplicationListener<AuthenticationSuccessEvent> {

    private static final Logger logger = LogManager.getLogger(LoginListener.class);
    @Autowired
    private UserRepository userRepository;

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        try {
            UserDetails ud = (UserDetails) event.getAuthentication().getPrincipal();

            userRepository.findByUsername(ud.getUsername()).ifPresent(user -> {
                user.setLastAccess(new Date());
                userRepository.save(user);
                logger.info("[USER : {}] ---> LOGIN SUCCESSFULLY", ud.getUsername());
            });
        }catch (Exception exception){
            logger.error("[USER : {}] ---> ERROR LOG LOGIN: {}", event.getAuthentication().getName(), exception.getMessage());
        }
    }
}
