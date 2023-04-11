package mx.edu.utez.warehouse.user.service;
import mx.edu.utez.warehouse.user.model.UserModel;
import mx.edu.utez.warehouse.message.model.MessageModel;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface UserService {
    MessageModel findAllUsers(Pageable page, String username, String uuid);
    MessageModel findById(long id, String username, String uuid);
    MessageModel registerUser(UserModel userModel, String username, String uuid);
    MessageModel updateUser(UserModel userModel, String username, String uuid);
    MessageModel disableUser(long id, String username, String uuid);

    List<UserModel> listWarehousers();
    List<UserModel> listInvoicers();

    MessageModel findByUsername(String username, String uuid);

    MessageModel updatePassword(String username, String uuid, String password);

}
