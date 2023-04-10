package mx.edu.utez.warehouse.warehouse.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import mx.edu.utez.warehouse.message.model.MessageModel;
import mx.edu.utez.warehouse.security.config.SecurityUser;
import mx.edu.utez.warehouse.user.model.UserModel;
import mx.edu.utez.warehouse.user.service.UserServiceImpl;
import mx.edu.utez.warehouse.utils.MessageCatalog;
import mx.edu.utez.warehouse.warehouse.model.WarehouseModel;
import mx.edu.utez.warehouse.warehouse.service.WarehouseServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/warehouse")
public class WarehouseController {
    private static final String SPRING_SECURITY_CONTEXT = "SPRING_SECURITY_CONTEXT";
    private static final Logger logger = LogManager.getLogger(WarehouseController.class);
    @Autowired
    WarehouseServiceImpl service;

    @Autowired
    UserServiceImpl serviceUser;

    List<UserModel> users = new LinkedList<>();

    @GetMapping("/list")
    public String findAllWarehouses(Model model, HttpSession httpSession, Pageable pageable, @ModelAttribute("warehouse") WarehouseModel warehouse) {

        UUID uuid = UUID.randomUUID();
        String username = "";

        try {
            SecurityContextImpl securityContext = (SecurityContextImpl) httpSession.getAttribute(SPRING_SECURITY_CONTEXT);
            SecurityUser user = (SecurityUser) securityContext.getAuthentication().getPrincipal();
            username = user.getUsername();
            logger.info("[USER : {}] || [UUID : {}] ---> EXECUTING WAREHOUSE MODULE ---> findAllWarehouse()", username, uuid);
            MessageModel warehouses = service.findAllWarehouse(pageable, username, uuid.toString());
            logger.info("[USER : {}] || [UUID : {}] ---> WAREHOUSE MODULE ---> findAllWarehouse() RESPONSE: {}", username, uuid, warehouses.getMessage());
            warehouses.setUuid(uuid.toString());

            model.addAttribute("result", warehouses);
            model.addAttribute("listUsers", serviceUser.listUsers());
//            model.addAttribute("listWarehouse", serviceUser.findUserByRole("WAREHOUSE"));
            if (warehouses.getIsError()) {
                return "errorPages/500";
            }
            model.addAttribute("pageSize", pageable.getPageSize());
            return "warehouse/warehouse";
        } catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> WAREHOUSE MODULE ---> findAllWarehouse() ERROR: {}", username, uuid, exception.getMessage());
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
            SecurityUser user = (SecurityUser) securityContext.getAuthentication().getPrincipal();
            username = user.getUsername();

            logger.info("[USER : {}] || [UUID : {}] ---> EXECUTING WAREHOUSE MODULE ---> findById()", username, uuid);
            MessageModel warehouse = service.findById(id, username, uuid.toString());
            warehouse.setUuid(uuid.toString());
            logger.info("[USER : {}] || [UUID : {}] ---> WAREHOUSE MODULE CONTROLLER---> findById() RESPONSE: {}", username, uuid, warehouse.getData() == null ? "null" : warehouse.getData().toString());

            return warehouse;
        }catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> WAREHOUSE MODULE ---> findById() ERROR: {}", username, uuid, exception.getMessage());
            MessageModel message = new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
            message.setUuid(uuid.toString());
            return message;
        }
    }

    @PostMapping("/save")
    public String saveWarehouse(@Valid @ModelAttribute("warehouse") WarehouseModel warehouse, BindingResult result, Model model, RedirectAttributes redirectAttributes, HttpSession httpSession, Pageable pageable) {
        UUID uuid = UUID.randomUUID();
        String username = "";
        try {
            SecurityContextImpl securityContext = (SecurityContextImpl) httpSession.getAttribute(SPRING_SECURITY_CONTEXT);
            SecurityUser user = (SecurityUser) securityContext.getAuthentication().getPrincipal();
            username = user.getUsername();
            logger.info("[USER : {}] || [UUID : {}] ---> EXECUTING WAREHOUSE MODULE ---> saveWarehouse()", username, uuid);
            model.addAttribute("action", "save");
            model.addAttribute("pageSize", pageable.getPageSize());

            if(service.isExistWarehouse(warehouse.getIdentifier())){
                result.rejectValue("identifier", "warehouse.identifier", "El identificador ya existe");
            }
            if(result.hasErrors()) {
                logger.error("[USER : {}] || [UUID : {}] ---> WAREHOUSE MODULE ---> saveWarehouse() ERROR: {}", username, uuid, result.getAllErrors());
                MessageModel warehouses = service.findAllWarehouse(pageable, username, uuid.toString());
                warehouses.setUuid(uuid.toString());
                warehouses.setMessage(MessageCatalog.VALIDATION_ERROR);
                warehouses.setIsError(true);
                model.addAttribute("result", warehouses);
                model.addAttribute("data", warehouse);

                return "warehouse/warehouse";
            }

            MessageModel warehouses = service.registerWarehouse(warehouse, username, uuid.toString());
//            for(UserModel userModel : users){
//                warehouse.getWarehouse().setId(userModel.getId());
//            }
//            for(UserModel userModel : users){
//                warehouse.getInvoice().setId(userModel.getId());
//            }
            warehouses.setUuid(uuid.toString());
            logger.info("[USER : {}] || [UUID : {}] ---> WAREHOUSE MODULE ---> saveWarehouse() RESPONSE: {}", username, uuid, warehouses.getMessage());

            if (warehouses.getIsError()){
                model.addAttribute("result", warehouses);
                return "errorPages/500";
            }
            redirectAttributes.addFlashAttribute("resultAction", warehouses);
            return "redirect:/warehouse/list";
        }catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> WAREHOUSE MODULE ---> saveWarehouse() ERROR: {}", username, uuid, exception.getMessage());
            MessageModel message = new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
            message.setUuid(uuid.toString());
            model.addAttribute("result", message);
            return "errorPages/500";
        }
    }

    @PostMapping("/update")
    public String updateWarehouse(@Valid @ModelAttribute("warehouse") WarehouseModel warehouse, BindingResult result, Model model, RedirectAttributes redirectAttributes, HttpSession httpSession, Pageable pageable) {
        UUID uuid = UUID.randomUUID();
        String username = "";
        try {
            SecurityContextImpl securityContext = (SecurityContextImpl) httpSession.getAttribute(SPRING_SECURITY_CONTEXT);
            SecurityUser user = (SecurityUser) securityContext.getAuthentication().getPrincipal();
            username = user.getUsername();
            logger.info("[USER : {}] || [UUID : {}] ---> EXECUTING WAREHOUSE MODULE ---> updateWarehouse()", username, uuid);

            if(service.isExistWarehouseAndId(warehouse.getIdentifier(), warehouse.getId())){
                result.rejectValue("identifier", "warehouse.identifier", "El identificador ya existe");
            }
            if(result.hasErrors()) {
                logger.error("[USER : {}] || [UUID : {}] ---> WAREHOUSE MODULE ---> updateWarehouse() ERROR: {}", username, uuid, result.getAllErrors());
                MessageModel warehouses = service.findAllWarehouse(pageable, username, uuid.toString());
                warehouses.setUuid(uuid.toString());
                warehouses.setMessage(MessageCatalog.VALIDATION_ERROR);
                warehouses.setIsError(true);
                model.addAttribute("result", warehouses);
                model.addAttribute("data", warehouse);
                model.addAttribute("action", "update");
                model.addAttribute("pageSize", pageable.getPageSize());

                return "warehouse/warehouse";
            }
            for(UserModel userModel : users){
                warehouse.getWarehouse().setId(userModel.getId());
            }
            for(UserModel userModel : users){
                warehouse.getInvoice().setId(userModel.getId());
            }

            MessageModel warehouses = service.updateWarehouse(warehouse, username, uuid.toString());


            warehouses.setUuid(uuid.toString());
            logger.info("[USER : {}] || [UUID : {}] ---> WAREHOUSE MODULE ---> updateWarehouse() RESPONSE: {}", username, uuid, warehouses.getMessage());
            if(warehouses.getIsError()) {
                model.addAttribute("result", warehouses);
                return "errorPages/500";
            }

            redirectAttributes.addFlashAttribute("pageSize", pageable.getPageSize());
            redirectAttributes.addFlashAttribute("resultAction", warehouses);
            return "redirect:/warehouse/list";
        }catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> WAREHOUSE MODULE ---> updateWarehouse() ERROR: {}", username, uuid, exception.getMessage());
            MessageModel message = new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
            message.setUuid(uuid.toString());
            model.addAttribute("result", message);
            return "errorPages/500";
        }
    }
}
