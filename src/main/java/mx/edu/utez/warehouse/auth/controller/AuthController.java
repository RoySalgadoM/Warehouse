package mx.edu.utez.warehouse.auth.controller;

import mx.edu.utez.warehouse.WarehouseApplication;
import mx.edu.utez.warehouse.message.model.MessageModel;
import mx.edu.utez.warehouse.user.model.UserModel;
import mx.edu.utez.warehouse.utils.MessageCatalog;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Controller
@RequestMapping("/auth")
public class AuthController {
    private static final Logger logger = LogManager.getLogger(WarehouseApplication.class);

    @PostMapping("/logins")
    public void login(Model model, @RequestBody UserModel user, BindingResult bindingResult){
        UUID uuid = UUID.randomUUID();
        logger.info("[UUID : " + uuid.toString() + "] ---> EXECUTING LOGIN");
        logger.info("[UUID : " + uuid.toString() + "] ---> EXECUTING LOGIN WITH USER: " + user.toString());
        MessageModel login = new MessageModel(MessageCatalog.SUCCESS_REGISTER, null, false, null);
        model.addAttribute("result", login);
    }
}
