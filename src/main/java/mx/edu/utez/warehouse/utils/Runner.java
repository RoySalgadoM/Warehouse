package mx.edu.utez.warehouse.utils;

import mx.edu.utez.warehouse.WarehouseApplication;
import mx.edu.utez.warehouse.person.model.PersonModel;
import mx.edu.utez.warehouse.person.service.PersonRepository;
import mx.edu.utez.warehouse.role.service.model.AuthorityName;
import mx.edu.utez.warehouse.role.service.model.RoleModel;
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
    private static final Logger logger = LogManager.getLogger(WarehouseApplication.class);

    @Autowired
    private RoleRepository roleInterface;
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
        }
        if(userRepository.count() == 0) {
            logger.info("CREATING ADMIN USER");
            PersonModel personModel = new PersonModel(1L, "Roy", "Salgado", "Martinez", "roy21rasm@gmail.com", "7771144520", "SAMR020621SL7", 1);
            personRepository.save(personModel);
            Set<RoleModel> roles = new HashSet<RoleModel>();
            roles.add(new RoleModel(1L, AuthorityName.ADMIN));

            userRepository.saveAndFlush(new UserModel(1L, "roysalgado", passwordEncoder.encode("rasm"), 1,  roles, personModel, new Date()));
        }
        logger.info("END RUNNER");


    }
}
