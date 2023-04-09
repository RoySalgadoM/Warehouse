package mx.edu.utez.warehouse.user.controller;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import mx.edu.utez.warehouse.area.model.AreaModel;
import mx.edu.utez.warehouse.message.model.MessageModel;
import mx.edu.utez.warehouse.role.model.AuthorityName;
import mx.edu.utez.warehouse.role.model.RoleModel;
import mx.edu.utez.warehouse.role.service.RoleServiceImpl;
import mx.edu.utez.warehouse.security.config.SecurityUser;
import mx.edu.utez.warehouse.user.model.UserModel;
import mx.edu.utez.warehouse.user.service.UserServiceImpl;
import mx.edu.utez.warehouse.utils.MessageCatalog;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/user")
public class UserController {
    private static final String SPRING_SECURITY_CONTEXT = "SPRING_SECURITY_CONTEXT";
    private static final Logger logger = LogManager.getLogger(UserController.class);
    @Autowired
    UserServiceImpl service;

    @Autowired
    private PasswordEncoder passwordEncoder;



    @Autowired
    RoleServiceImpl roleService;


    @GetMapping("/list")
    public String findAllUsers(Model model, HttpSession httpSession, Pageable pageable, @ModelAttribute("user")UserModel user) {
        UUID uuid = UUID.randomUUID();
        String username = "";
        try{
            SecurityContextImpl securityContext = (SecurityContextImpl) httpSession.getAttribute(SPRING_SECURITY_CONTEXT);
            SecurityUser user1 = (SecurityUser) securityContext.getAuthentication().getPrincipal();
            username = user1.getUsername();
            logger.info("[USER : {}] || [UUID : {}] ---> EXECUTING USER MODULE ---> findAllUsers()", username, uuid);
            MessageModel users = service.findAllUsers(pageable, username, uuid.toString());
            logger.info("[USER : {}] || [UUID : {}] ---> USER MODULE ---> findAllUsers() RESPONSE: {}", username, uuid, users.getMessage());
            users.setUuid(uuid.toString());
            System.out.println(users);
            model.addAttribute("result", users);
            model.addAttribute("listRole", roleService.findRoles());
            if (users.getIsError()) {
                return "errorPages/500";
            }
            model.addAttribute("pageSize", pageable.getPageSize());
            return "user/user";
        }catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> USER MODULE ---> findAllUsers() ERROR: {}", username, uuid, exception.getMessage());
            MessageModel message = new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
            message.setUuid(uuid.toString());
            model.addAttribute("result", message);
            return "errorPages/500";
        }


    }


    @GetMapping("/{id}")
    @ResponseBody
    public MessageModel findById(HttpSession httpSession, @PathVariable("id") Long id) {
        UUID uuid = UUID.randomUUID();
        String username = "";
        try {
            SecurityContextImpl securityContext = (SecurityContextImpl) httpSession.getAttribute(SPRING_SECURITY_CONTEXT);
            SecurityUser user1 = (SecurityUser) securityContext.getAuthentication().getPrincipal();
            username = user1.getUsername();


            logger.info("[USER : {}] || [UUID : {}] ---> EXECUTING USER MODULE ---> findById()", username, uuid);
            MessageModel user= service.findById(id, username, uuid.toString());
            user.setUuid(uuid.toString());
            logger.info("[USER : {}] || [UUID : {}] ---> USER MODULE ---> findById() RESPONSE: {}", username, uuid, user.getData() == null ? "null" : user.getData().toString());


            return user;
        }catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> USER MODULE ---> findById() ERROR: {}", username, uuid, exception.getMessage());
            MessageModel message = new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
            message.setUuid(uuid.toString());
            return message;
        }
    }


    @PostMapping("/save")
    public String saveUser(@Valid @ModelAttribute("user") UserModel user,BindingResult result, Model model, RedirectAttributes redirectAttributes, HttpSession httpSession, Pageable pageable) {
        UUID uuid = UUID.randomUUID();
        String username = "";
        try {
            SecurityContextImpl securityContext = (SecurityContextImpl) httpSession.getAttribute(SPRING_SECURITY_CONTEXT);
            SecurityUser user1 = (SecurityUser) securityContext.getAuthentication().getPrincipal();
            username = user1.getUsername();
            System.out.println(user);
            System.out.println(user1);
            logger.info("[USER : {}] || [UUID : {}] ---> EXECUTING USER MODULE ---> saveUser()", username, uuid);
            model.addAttribute("action", "save");
            model.addAttribute("pageSize", pageable.getPageSize());
            if(service.isExistUser(user.getUsername())){
                result.rejectValue("username", "user.username", "El usuario ya existe");
            }

            if(result.hasErrors()) {
                logger.error("[USER : {}] || [UUID : {}] ---> AREA MODULE ---> saveUser() ERROR: {}", username, uuid, result.getAllErrors());
                MessageModel users = service.findAllUsers(pageable, username, uuid.toString());
                users.setUuid(uuid.toString());
                users.setMessage(MessageCatalog.VALIDATION_ERROR);
                users.setIsError(true);
                model.addAttribute("result", users);
                model.addAttribute("data", user);
                model.addAttribute("listRole", roleService.findRoles());
                return "user/user";
            }

            Set<RoleModel> roles = user.getAuthorities();
            roles.addAll(roleService.findRolesByIds(user.getAuthorities().stream().map(RoleModel::getId).collect(Collectors.toList())));
            user.setAuthorities(roles);
            user.setPassword(user.getUsername());
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);

            MessageModel users = service.registerUser(user,username, uuid.toString());
            users.setUuid(uuid.toString());
            logger.info("[USER : {}] || [UUID : {}] ---> USER MODULE ---> saveUser() RESPONSE: {}", username, uuid, users.getMessage());
            if (users.getIsError()){
                model.addAttribute("result", users);
                return "errorPages/500";
            }
            redirectAttributes.addFlashAttribute("resultAction", users);
            return "redirect:/user/list";
        }catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> USER MODULE ---> saveUser() ERROR: {}", username, uuid, exception.getMessage());
            MessageModel message = new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
            message.setUuid(uuid.toString());
            model.addAttribute("result", message);
            return "errorPages/500";
        }
    }


    @PostMapping("/update")
    public String updateUser(@Valid @ModelAttribute("user") UserModel user, BindingResult result, Model model, RedirectAttributes redirectAttributes, HttpSession httpSession, Pageable pageable) {
        UUID uuid = UUID.randomUUID();
        String username = "";
        try {
            SecurityContextImpl securityContext = (SecurityContextImpl) httpSession.getAttribute(SPRING_SECURITY_CONTEXT);
            SecurityUser user1 = (SecurityUser) securityContext.getAuthentication().getPrincipal();
            username = user1.getUsername();
            logger.info("[USER : {}] || [UUID : {}] ---> EXECUTING USER MODULE ---> updateUser()", username, uuid);

            if(service.isExistUserAndId(user.getUsername(), user.getId())){
                result.rejectValue("username", "user.username", "El usuario ya existe");
            }

            if (user.getAuthorities().contains(2) && user.getAuthorities().contains(3)) {
                result.rejectValue("authorities", "user.authorities", "El usuario no puede tener los roles de almacenista y facturación");
            }

            if (user.getAuthorities().contains(2L) && user.getAuthorities().contains(3L)) {
                result.rejectValue("authorities", "user.authorities", "El usuario no puede tener los roles de almacenista y facturación");
            }
            if (user.getAuthorities().contains(AuthorityName.INVOICER) && user.getAuthorities().contains(AuthorityName.WAREHOUSER)) {
                result.rejectValue("roles", "user.roles", "El usuario no puede tener los roles de almacenista y facturación");
            }



            if(result.hasErrors()) {
                logger.error("[USER : {}] || [UUID : {}] ---> AREA MODULE ---> updateArea() ERROR: {}", username, uuid, result.getAllErrors());
                MessageModel users = service.findAllUsers(pageable, username, uuid.toString());
                users.setUuid(uuid.toString());
                users.setMessage(MessageCatalog.VALIDATION_ERROR);
                users.setIsError(true);
                model.addAttribute("result", users);
                model.addAttribute("data", user);
                model.addAttribute("listRole", roleService.findRoles());
                model.addAttribute("action", "update");
                model.addAttribute("pageSize", pageable.getPageSize());


                return "user/user";
            }
            List<RoleModel> roles = roleService.findRolesByIds(user.getAuthorities().stream().map(RoleModel::getId).collect(Collectors.toList()));
            MessageModel users = service.updateUser(user, username, uuid.toString());
            users.setUuid(uuid.toString());


            logger.info("[USER : {}] || [UUID : {}] ---> AREA MODULE ---> updateUser() RESPONSE: {}", username, uuid, users.getMessage());
            if(users.getIsError()) {
                model.addAttribute("result", users);
                return "errorPages/500";
            }
            redirectAttributes.addFlashAttribute("pageSize", pageable.getPageSize());
            redirectAttributes.addFlashAttribute("resultAction", users);
            redirectAttributes.addFlashAttribute("roles", roles);
            return "redirect:/user/list";
        }catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> USER MODULE ---> updateArea() ERROR: {}", username, uuid, exception.getMessage());
            MessageModel message = new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
            message.setUuid(uuid.toString());
            model.addAttribute("result", message);
            return "errorPages/500";
        }
    }
















    @PostMapping("/disable")
    public String disableUser(Model model, @RequestParam long id, RedirectAttributes redirectAttributes, HttpSession httpSession) {
        UUID uuid = UUID.randomUUID();
        String username = "";
        try {
            SecurityContextImpl securityContext = (SecurityContextImpl) httpSession.getAttribute(SPRING_SECURITY_CONTEXT);
            SecurityUser user = (SecurityUser) securityContext.getAuthentication().getPrincipal();
            username = user.getUsername();
            logger.info("[USER : {}] || [UUID : {}] ---> EXECUTING USER MODULE ---> disableUser()", username, uuid);
            MessageModel users = service.disableUser(id, username, uuid.toString());
            logger.info("[USER : {}] || [UUID : {}] ---> USER MODULE ---> disableUser() RESPONSE: {}", username, uuid, users);
            users.setUuid(uuid.toString());


            if (users.getIsError()) {
                model.addAttribute("result", users);
                return "errorPages/500";
            }
            redirectAttributes.addFlashAttribute("resultAction", users);
            return "redirect:/user/list";
        } catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> USER MODULE ---> disableUser() ERROR: {}", username, uuid, exception.getMessage());
            MessageModel message = new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
            message.setUuid(uuid.toString());
            model.addAttribute("result", message);
            return "errorPages/500";
        }


    }


}

