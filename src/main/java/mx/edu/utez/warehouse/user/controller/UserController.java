package mx.edu.utez.warehouse.user.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.thymeleaf.util.ArrayUtils;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/user")
public class UserController {
    private static final String SPRING_SECURITY_CONTEXT = "SPRING_SECURITY_CONTEXT";
    private static final Logger logger = LogManager.getLogger(UserController.class);
    private static final String RESULT = "result";
    private static final String LIST_ROLE = "listRole";
    private static final String ERROR_PAGE_500 = "errorPages/500";
    private static final String PAGE_SIZE = "pageSize";
    private static final String REDIRECT_USER = "user/user";
    private static final String AUTHORITIES = "authorities";
    private static final String AUTHORITIES_ERROR_CODE = "user.authorities";
    private static final String ERROR_ASIGN_ROLE = "Los roles asignados no son válidos";
    private static final String ROLES = "roles";
    private static final String RESULT_ACTION = "resultAction";
    private static final String REDIRECT_USER_LIST = "redirect:/user/list";


    @Autowired
    UserServiceImpl service;
    @Autowired
    RoleServiceImpl roleService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/list")
    public String findAllUsers(Model model, HttpSession httpSession, Pageable pageable, @ModelAttribute("user") UserModel user) {
        UUID uuid = UUID.randomUUID();
        String username = "";
        try {
            SecurityContextImpl securityContext = (SecurityContextImpl) httpSession.getAttribute(SPRING_SECURITY_CONTEXT);
            SecurityUser user1 = (SecurityUser) securityContext.getAuthentication().getPrincipal();
            username = user1.getUsername();
            logger.info("[USER : {}] || [UUID : {}] ---> EXECUTING USER MODULE ---> findAllUsers()", username, uuid);
            MessageModel users = service.findAllUsers(pageable, username, uuid.toString());
            logger.info("[USER : {}] || [UUID : {}] ---> USER MODULE ---> findAllUsers() RESPONSE: {}", username, uuid, users.getMessage());
            users.setUuid(uuid.toString());
            model.addAttribute(RESULT, users);
            model.addAttribute(LIST_ROLE, roleService.findRoles());
            if (users.getIsError()) {
                return ERROR_PAGE_500;
            }
            model.addAttribute(PAGE_SIZE, pageable.getPageSize());
            return REDIRECT_USER;
        } catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> USER MODULE ---> findAllUsers() ERROR: {}", username, uuid, exception.getMessage());
            MessageModel message = new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
            message.setUuid(uuid.toString());
            model.addAttribute(RESULT, message);
            return ERROR_PAGE_500;
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
            MessageModel user = service.findById(id, username, uuid.toString());
            user.setUuid(uuid.toString());
            logger.info("[USER : {}] || [UUID : {}] ---> USER MODULE ---> findById() RESPONSE: {}", username, uuid, user.getData() == null ? "null" : user.getData().toString());


            return user;
        } catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> USER MODULE ---> findById() ERROR: {}", username, uuid, exception.getMessage());
            MessageModel message = new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
            message.setUuid(uuid.toString());
            return message;
        }
    }


    @PostMapping("/save")
    public String saveUser(@Valid @ModelAttribute("user") UserModel user, BindingResult result, Model model, RedirectAttributes redirectAttributes, HttpSession httpSession, Pageable pageable) {
        UUID uuid = UUID.randomUUID();
        String username = "";
        try {
            SecurityContextImpl securityContext = (SecurityContextImpl) httpSession.getAttribute(SPRING_SECURITY_CONTEXT);
            SecurityUser user1 = (SecurityUser) securityContext.getAuthentication().getPrincipal();
            username = user1.getUsername();
            logger.info("[USER : {}] || [UUID : {}] ---> EXECUTING USER MODULE ---> saveUser()", username, uuid);
            model.addAttribute("action", "save");
            model.addAttribute(PAGE_SIZE, pageable.getPageSize());
            if (service.isExistUser(user.getUsername())) {
                result.rejectValue("username", "user.username", "El usuario ya existe");
            }

            if (user.getAuthorities() == null) {
                result.rejectValue(AUTHORITIES, "user.authorities", "Los roles son obligatorios");
            } else {
                user.getAuthorities().stream().forEach(roleModel -> {
                    if (!(roleModel instanceof RoleModel)){
                        result.rejectValue(AUTHORITIES, AUTHORITIES_ERROR_CODE, ERROR_ASIGN_ROLE);
                        return;
                    }
                    if (!(ArrayUtils.contains(new Long[]{1L, 2L, 3L}, roleModel.getId())))
                        result.rejectValue(AUTHORITIES, AUTHORITIES_ERROR_CODE, ERROR_ASIGN_ROLE);
                });
                if (user.getAuthorities().contains(2) && user.getAuthorities().contains(3)) {
                    result.rejectValue(AUTHORITIES, AUTHORITIES_ERROR_CODE, "El usuario no puede tener los roles de almacenista y facturación");
                }

                if (user.getAuthorities().contains(2L) && user.getAuthorities().contains(3L)) {
                    result.rejectValue(AUTHORITIES, AUTHORITIES_ERROR_CODE, "El usuario no puede tener los roles de almacenista y facturación");
                }
                if (user.getAuthorities().contains(AuthorityName.INVOICER) && user.getAuthorities().contains(AuthorityName.WAREHOUSER)) {
                    result.rejectValue(ROLES, "user.roles", "El usuario no puede tener los roles de almacenista y facturación");
                }

            }

            if (result.hasErrors()) {
                logger.error("[USER : {}] || [UUID : {}] ---> AREA MODULE ---> saveUser() ERROR: {}", username, uuid, result.getAllErrors());
                MessageModel users = service.findAllUsers(pageable, username, uuid.toString());
                users.setUuid(uuid.toString());
                users.setMessage(MessageCatalog.VALIDATION_ERROR);
                users.setIsError(true);
                model.addAttribute(RESULT, users);
                model.addAttribute("data", user);
                model.addAttribute(LIST_ROLE, roleService.findRoles());
                return REDIRECT_USER;
            }

            Set<RoleModel> roles = user.getAuthorities();
            roles.addAll(roleService.findRolesByIds(user.getAuthorities().stream().map(RoleModel::getId).collect(Collectors.toList())));
            user.setAuthorities(roles);
            user.setPassword(user.getUsername());
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);

            MessageModel users = service.registerUser(user, username, uuid.toString());
            users.setUuid(uuid.toString());
            logger.info("[USER : {}] || [UUID : {}] ---> USER MODULE ---> saveUser() RESPONSE: {}", username, uuid, users.getMessage());
            if (users.getIsError()) {
                model.addAttribute(RESULT, users);
                return ERROR_PAGE_500;
            }
            redirectAttributes.addFlashAttribute(RESULT_ACTION, users);
            return REDIRECT_USER_LIST;
        } catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> USER MODULE ---> saveUser() ERROR: {}", username, uuid, exception.getMessage());
            MessageModel message = new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
            message.setUuid(uuid.toString());
            model.addAttribute(RESULT, message);
            return ERROR_PAGE_500;
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

            if (user.getAuthorities() == null) {
                result.rejectValue(AUTHORITIES, AUTHORITIES_ERROR_CODE, "Los roles son obligatorios");
            } else {
                user.getAuthorities().stream().forEach(roleModel -> {
                    if (!(roleModel instanceof RoleModel)){
                        result.rejectValue(AUTHORITIES, AUTHORITIES_ERROR_CODE, ERROR_ASIGN_ROLE);
                        return;
                    }
                    if (!(ArrayUtils.contains(new Long[]{1L, 2L, 3L}, roleModel.getId())))
                        result.rejectValue(AUTHORITIES, AUTHORITIES_ERROR_CODE, ERROR_ASIGN_ROLE);
                });
                if (user.getAuthorities().contains(2) && user.getAuthorities().contains(3)) {
                    result.rejectValue(AUTHORITIES, AUTHORITIES_ERROR_CODE, "El usuario no puede tener los roles de almacenista y facturación");
                }

                if (user.getAuthorities().contains(2L) && user.getAuthorities().contains(3L)) {
                    result.rejectValue(AUTHORITIES, AUTHORITIES_ERROR_CODE, "El usuario no puede tener los roles de almacenista y facturación");
                }
                if (user.getAuthorities().contains(AuthorityName.INVOICER) && user.getAuthorities().contains(AuthorityName.WAREHOUSER)) {
                    result.rejectValue(ROLES, "user.roles", "El usuario no puede tener los roles de almacenista y facturación");
                }

            }

            if (service.isExistUserAndId(user.getUsername(), user.getId())) {
                result.rejectValue("username", "user.username", "El usuario ya existe");
            }

            if (result.hasErrors()) {
                logger.error("[USER : {}] || [UUID : {}] ---> AREA MODULE ---> updateArea() ERROR: {}", username, uuid, result.getAllErrors());
                MessageModel users = service.findAllUsers(pageable, username, uuid.toString());
                users.setUuid(uuid.toString());
                users.setMessage(MessageCatalog.VALIDATION_ERROR);
                users.setIsError(true);
                model.addAttribute(RESULT, users);
                model.addAttribute("data", user);
                model.addAttribute(LIST_ROLE, roleService.findRoles());
                model.addAttribute("action", "update");
                model.addAttribute(PAGE_SIZE, pageable.getPageSize());


                return REDIRECT_USER;
            }
            List<RoleModel> roles = roleService.findRolesByIds(user.getAuthorities().stream().map(RoleModel::getId).collect(Collectors.toList()));
            MessageModel users = service.updateUser(user, username, uuid.toString());
            users.setUuid(uuid.toString());


            logger.info("[USER : {}] || [UUID : {}] ---> AREA MODULE ---> updateUser() RESPONSE: {}", username, uuid, users.getMessage());
            if (users.getIsError()) {
                model.addAttribute(RESULT, users);
                return ERROR_PAGE_500;
            }
            redirectAttributes.addFlashAttribute(PAGE_SIZE, pageable.getPageSize());
            redirectAttributes.addFlashAttribute(RESULT_ACTION, users);
            redirectAttributes.addFlashAttribute(ROLES, roles);
            return REDIRECT_USER_LIST;
        } catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> USER MODULE ---> updateArea() ERROR: {}", username, uuid, exception.getMessage());
            MessageModel message = new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
            message.setUuid(uuid.toString());
            model.addAttribute(RESULT, message);
            return ERROR_PAGE_500;
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
                model.addAttribute(RESULT, users);
                return ERROR_PAGE_500;
            }
            redirectAttributes.addFlashAttribute(RESULT_ACTION, users);
            return REDIRECT_USER_LIST;
        } catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> USER MODULE ---> disableUser() ERROR: {}", username, uuid, exception.getMessage());
            MessageModel message = new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
            message.setUuid(uuid.toString());
            model.addAttribute(RESULT, message);
            return ERROR_PAGE_500;
        }


    }


}

