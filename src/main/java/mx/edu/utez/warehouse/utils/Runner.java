package mx.edu.utez.warehouse.utils;

import mx.edu.utez.warehouse.order_status.model.OrderStatusModel;
import mx.edu.utez.warehouse.order_status.model.OrderStatusName;
import mx.edu.utez.warehouse.order_status.service.OrderStatusRepository;
import mx.edu.utez.warehouse.person.model.PersonModel;
import mx.edu.utez.warehouse.person.service.PersonRepository;
import mx.edu.utez.warehouse.role.model.AuthorityName;
import mx.edu.utez.warehouse.role.model.RoleModel;
import mx.edu.utez.warehouse.role.service.RoleRepository;
import mx.edu.utez.warehouse.user.model.UserModel;
import mx.edu.utez.warehouse.user.service.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Configuration
@Component
public class Runner implements CommandLineRunner {
    private static final Logger logger = LogManager.getLogger(Runner.class);

    @Autowired
    private RoleRepository roleInterface;

    @Autowired
    private OrderStatusRepository orderStatusRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    PersonRepository personRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Override
    public void run(String... args) throws Exception {
        logger.info("STARTING RUNNER");
        if(roleInterface.count() == 0){
            logger.info("CREATING ROLES");
            roleInterface.save(new RoleModel(1L, AuthorityName.ADMIN));
            roleInterface.save(new RoleModel(2L, AuthorityName.WAREHOUSER));
            roleInterface.save(new RoleModel(3L, AuthorityName.INVOICER));
            orderStatusRepository.save(new OrderStatusModel(1L, OrderStatusName.PENDING));
            orderStatusRepository.save(new OrderStatusModel(2L, OrderStatusName.SENT));
            orderStatusRepository.save(new OrderStatusModel(3L, OrderStatusName.DELIVERED));
            orderStatusRepository.save(new OrderStatusModel(4L, OrderStatusName.CANCELED));

        }
        if(userRepository.count() == 0) {
            logger.info("CREATING ADMIN USER");
            PersonModel personModel = new PersonModel(1L, "Roy", "Salgado", "Martinez", "roy21rasm@gmail.com", "7771144520", "SAMR020621SL7", 1);
            personRepository.save(personModel);
            Set<RoleModel> roles = new HashSet<>();
            roles.add(new RoleModel(1L, AuthorityName.ADMIN));

            userRepository.saveAndFlush(new UserModel(1L, "roysalgado", passwordEncoder.encode("rasm"), 1,  roles, personModel, new Date(), "roy21rasm@gmail.com"));
        }
        logger.info("END RUNNER");


    }
}
