package mx.edu.utez.warehouse.user.service;
import jakarta.persistence.NoResultException;
import mx.edu.utez.warehouse.message.model.MessageModel;
import mx.edu.utez.warehouse.role.model.RoleModel;
import mx.edu.utez.warehouse.role.service.RoleRepository;
import mx.edu.utez.warehouse.role.service.RoleService;
import mx.edu.utez.warehouse.user.model.UserModel;
import mx.edu.utez.warehouse.utils.MessageCatalog;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class UserServiceImpl implements UserService {


    private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);


    @Autowired
    UserRepository repository;
    @Autowired
    private PasswordEncoder passwordEncoder;



    @Autowired
    RoleRepository roleRepository;
    @Override
    public MessageModel findAllUsers(Pageable page, String username, String uuid) {
        try {
            Page<UserModel> users= repository.findAll(page);
            if(users.getNumberOfElements()==0){
                return new MessageModel(MessageCatalog.NO_RECORDS_FOUND,null,false);
            }
            return new MessageModel(MessageCatalog.RECORDS_FOUND,users,false);
        }catch (Exception exception){
            logger.error("[USER : {}] || [UUID : {}] ---> USER MODULE ---> findAllUsers() ERROR: {}", username, uuid, exception.getMessage());
            return new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
        }
    }


    @Override
    public MessageModel findById(long id, String username, String uuid) {
        try {
            var findUser = repository.findById(id);
            if (findUser.isEmpty()) {
                throw new NoResultException("The area could not be found");
            }
            return new MessageModel(MessageCatalog.RECORDS_FOUND, findUser.get(), false);


        } catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> USER MODULE ---> findById() ERROR: {}", username, uuid, exception.getMessage());
            return new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
        }
    }


    @Override
    public MessageModel updateUser(UserModel userModel, String username, String uuid) {
        try {
            var user= repository.findById(userModel.getId());
            if (user.isEmpty()) {
                throw new NoResultException("The area could not be found");
            }
            user.get().setName(userModel.getName());
            user.get().setSurname(userModel.getSurname());
            user.get().setSecondSurname(userModel.getSecondSurname());
            user.get().setPhone(userModel.getPhone());
            user.get().setRfc(userModel.getRfc());
            user.get().setUsername(userModel.getUsername());
            user.get().setEmail(userModel.getEmail());
            Set<RoleModel> newAuthorities = new HashSet<>(userModel.getAuthorities());
            user.get().getAuthorities().clear();
            user.get().getAuthorities().addAll(newAuthorities);
            repository.saveAndFlush(user.get());
            return new MessageModel(MessageCatalog.SUCCESS_UPDATE, null, false);


        } catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> USER MODULE ---> updateUser() ERROR: {}", username, uuid, exception.getMessage());
            return new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
        }
    }


    @Override
    public MessageModel registerUser(UserModel userModel, String username, String uuid) {
        MessageModel messageModel;
        try {
            userModel.setStatus(1);
            Set<RoleModel> roles = userModel.getAuthorities();
            roles.addAll(roleRepository.findByIdIn(userModel.getAuthorities().stream().map(RoleModel::getId).collect(Collectors.toList())));
            userModel.setAuthorities(roles);
            userModel.setPassword(userModel.getUsername());
            String encodedPassword = passwordEncoder.encode(userModel.getPassword());
            userModel.setPassword(encodedPassword);

            repository.save(userModel);
            messageModel = new MessageModel(MessageCatalog.SUCCESS_REGISTER, null, false);


        } catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> USER MODULE ---> saveUser() ERROR: {}", username, uuid, exception.getMessage());
            messageModel = new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
        }
        return messageModel;
    }




    @Override
    public MessageModel disableUser(long id, String username, String uuid) {
        try {
            var user = repository.findById(id);
            if (user.isEmpty()) {
                throw new NoResultException("The area could not be found");
            }
            user.get().setStatus(user.get().getStatus() == 1 ? 0 : 1);
            repository.saveAndFlush(user.get());
            return new MessageModel(user.get().getStatus() == 1 ? MessageCatalog.SUCCESS_ENABLE : MessageCatalog.SUCCESS_DISABLE, null, false);


        } catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> USER MODULE ---> disableUser() ERROR: {}", username, uuid, exception.getMessage());
            return new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
        }
    }


    public boolean isExistUser(String username) {
        return repository.existsByUsername(username);
    }
    public boolean isExistUserAndId(String username, Long id) {
        return repository.existsByUsernameAndIdNotLike(username, id);
    }
}
