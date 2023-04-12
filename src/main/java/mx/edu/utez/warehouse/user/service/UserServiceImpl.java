package mx.edu.utez.warehouse.user.service;
import jakarta.persistence.NoResultException;
import mx.edu.utez.warehouse.message.model.MessageModel;
import mx.edu.utez.warehouse.role.model.RoleModel;
import mx.edu.utez.warehouse.role.service.RoleRepository;
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
import java.util.Set;


@Service
public class UserServiceImpl implements UserService {


    private static final Logger logger = LogManager.getLogger(UserServiceImpl.class);


    @Autowired
    UserRepository repository;
    @Autowired
    PasswordEncoder passwordEncoder;
    private static final String USER_NOT_FOUND = "The user could not be found";

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
                throw new NoResultException(USER_NOT_FOUND);
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
                throw new NoResultException(USER_NOT_FOUND);
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
            roles.addAll(roleRepository.findByIdIn(userModel.getAuthorities().stream().map(RoleModel::getId).toList()));
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
                throw new NoResultException(USER_NOT_FOUND);
            }
            user.get().setStatus(user.get().getStatus() == 1 ? 0 : 1);
            repository.saveAndFlush(user.get());
            return new MessageModel(user.get().getStatus() == 1 ? MessageCatalog.SUCCESS_ENABLE : MessageCatalog.SUCCESS_DISABLE, null, false);


        } catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> USER MODULE ---> disableUser() ERROR: {}", username, uuid, exception.getMessage());
            return new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
        }
    }

    @Override
    public List<UserModel> listWarehousers() {
        RoleModel role = new RoleModel(2L);
        return repository.findAllByAuthoritiesAndStatus(role, 1L);
    }
    @Override
    public List<UserModel> listInvoicers() {
        RoleModel role = new RoleModel(3L);
        return repository.findAllByAuthoritiesAndStatus(role, 1L);
    }




    public boolean isExistUser(String username) {
        return repository.existsByUsername(username);
    }
    public boolean isExistUserAndId(String username, Long id) {
        return repository.existsByUsernameAndIdNotLike(username, id);
    }

    public UserModel findByIdAndAuthorities(Long id, RoleModel role) {
        return repository.findByIdAndAndAuthorities(id, role);
    }

    public MessageModel findByUsername(String username, String uuid){
        try {
            var user = repository.findByUsername(username).orElse(null);
            return new MessageModel(MessageCatalog.RECORDS_FOUND, user, false);
        } catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> USER MODULE ---> findByUsername() ERROR: {}", username, uuid, exception.getMessage());
            return new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
        }
    }

    @Override
    public MessageModel updatePassword(String username, String uuid, String password) {
        try {
            var user = repository.findByUsername(username);
            if (user.isEmpty()) {
                throw new NoResultException(USER_NOT_FOUND);
            }
            user.get().setPassword(passwordEncoder.encode(password));
            return new MessageModel(MessageCatalog.SUCCESS_UPDATE, null, false);
        } catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> USER MODULE ---> updatePassword() ERROR: {}", username, uuid, exception.getMessage());
            return new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
        }
    }

    public UserModel findByUsr(String username){
        return repository.findByUsername(username).orElse(null);
    }
}
