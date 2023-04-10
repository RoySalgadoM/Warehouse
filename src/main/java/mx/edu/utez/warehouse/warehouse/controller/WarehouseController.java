package mx.edu.utez.warehouse.warehouse.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import mx.edu.utez.warehouse.log.service.LogServiceImpl;
import mx.edu.utez.warehouse.message.model.MessageModel;
import mx.edu.utez.warehouse.role.model.RoleModel;
import mx.edu.utez.warehouse.role.service.RoleServiceImpl;
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
    private static final String RESULT = "result";
    private static final String WAREHOUSER_LIST = "listWarehousers";
    private static final String INVOICER_LIST = "listInvoicers";
    private static final String ERROR_PAGE_500 = "errorPages/500";
    private static final String PAGE_SIZE = "pageSize";
    private static final String WAREHOUSE_REDIRECT = "warehouse/warehouse";
    @Autowired
    WarehouseServiceImpl service;

    @Autowired
    UserServiceImpl serviceUser;

    @Autowired
    RoleServiceImpl serviceRole;
    @Autowired
    LogServiceImpl logService;

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

            model.addAttribute(RESULT, warehouses);
            model.addAttribute(WAREHOUSER_LIST, serviceUser.listWarehousers());
            model.addAttribute(INVOICER_LIST, serviceUser.listInvoicers());
            if (warehouses.getIsError()) {
                return ERROR_PAGE_500;
            }
            model.addAttribute(PAGE_SIZE, pageable.getPageSize());
            logService.saveLog("FINDING WAREHOUSES", 0L, username, uuid.toString());

            return WAREHOUSE_REDIRECT;
        } catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> WAREHOUSE MODULE ---> findAllWarehouse() ERROR: {}", username, uuid, exception.getMessage());
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
            SecurityUser user = (SecurityUser) securityContext.getAuthentication().getPrincipal();
            username = user.getUsername();

            logger.info("[USER : {}] || [UUID : {}] ---> EXECUTING WAREHOUSE MODULE ---> findById()", username, uuid);
            MessageModel warehouse = service.findById(id, username, uuid.toString());
            warehouse.setUuid(uuid.toString());
            logger.info("[USER : {}] || [UUID : {}] ---> WAREHOUSE MODULE CONTROLLER---> findById() RESPONSE: {}", username, uuid, warehouse.getData() == null ? "null" : warehouse.getData().toString());
            logService.saveLog("FINDING WAREHOUSE BY ID", id, username, uuid.toString());
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
            model.addAttribute(PAGE_SIZE, pageable.getPageSize());

            if(service.isExistWarehouse(warehouse.getIdentifier())){
                result.rejectValue("identifier", "warehouse.identifier", "El identificador ya existe");
            }
            if(warehouse.getWarehouser() == null || warehouse.getInvoicer() == null){
                if (warehouse.getWarehouser() == null) {
                    result.rejectValue("warehouse", "warehouse.warehouse", "El almacenista es requerido");
                }
                if (warehouse.getInvoicer() == null) {
                    result.rejectValue("invoice", "warehouse.invoice", "El facturador es requerido");
                }
            }else{
                RoleModel wareRole = new RoleModel(2L);
                RoleModel invoRole = new RoleModel(3L);
                var ware = serviceUser.findByIdAndAuthorities(warehouse.getWarehouser().getId(), wareRole);
                var invo = serviceUser.findByIdAndAuthorities(warehouse.getInvoicer().getId(), invoRole);

                if(ware == null){
                    result.rejectValue("warehouser", "warehouse.warehouser", "El usuario que asignó no es un almacenista o no existe");
                }
                if(invo == null){
                    result.rejectValue("invoicer", "warehouse.invoicer", "El usuario que asignó no es un facturador o no existe");
                }
            }

            if(result.hasErrors()) {
                logger.error("[USER : {}] || [UUID : {}] ---> WAREHOUSE MODULE ---> saveWarehouse() ERROR: {}", username, uuid, result.getAllErrors());
                MessageModel warehouses = service.findAllWarehouse(pageable, username, uuid.toString());
                warehouses.setUuid(uuid.toString());
                warehouses.setMessage(MessageCatalog.VALIDATION_ERROR);
                warehouses.setIsError(true);
                model.addAttribute(RESULT, warehouses);
                model.addAttribute("data", warehouse);
                model.addAttribute(WAREHOUSER_LIST, serviceUser.listWarehousers());
                model.addAttribute(INVOICER_LIST, serviceUser.listInvoicers());
                return WAREHOUSE_REDIRECT;
            }

            MessageModel warehouses = service.registerWarehouse(warehouse, username, uuid.toString());
            warehouses.setUuid(uuid.toString());
            logger.info("[USER : {}] || [UUID : {}] ---> WAREHOUSE MODULE ---> saveWarehouse() RESPONSE: {}", username, uuid, warehouses.getMessage());

            if (warehouses.getIsError()){
                model.addAttribute(RESULT, warehouses);
                return ERROR_PAGE_500;
            }
            redirectAttributes.addFlashAttribute("resultAction", warehouses);
            logService.saveLog("SAVING NEW WAREHOUSE", 0L, username, uuid.toString());
            return "redirect:/warehouse/list";
        }catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> WAREHOUSE MODULE ---> saveWarehouse() ERROR: {}", username, uuid, exception.getMessage());
            MessageModel message = new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
            message.setUuid(uuid.toString());
            model.addAttribute(RESULT, message);
            return ERROR_PAGE_500;
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

            if(warehouse.getWarehouser() == null || warehouse.getInvoicer() == null){
                if (warehouse.getWarehouser() == null) {
                    result.rejectValue("warehouse", "warehouse.warehouse", "El almacenista es requerido");
                }
                if (warehouse.getInvoicer() == null) {
                    result.rejectValue("invoice", "warehouse.invoice", "El facturador es requerido");
                }
            }else{
                RoleModel wareRole = new RoleModel(2L);
                RoleModel invoRole = new RoleModel(3L);
                var ware = serviceUser.findByIdAndAuthorities(warehouse.getWarehouser().getId(), wareRole);
                var invo = serviceUser.findByIdAndAuthorities(warehouse.getInvoicer().getId(), invoRole);

                if(ware == null){
                    result.rejectValue("warehouser", "warehouse.warehouser", "El usuario que asignó no es un almacenista o no existe");
                }
                if(invo == null){
                    result.rejectValue("invoicer", "warehouse.invoicer", "El usuario que asignó no es un facturador o no existe");
                }
            }
            if(result.hasErrors()) {
                logger.error("[USER : {}] || [UUID : {}] ---> WAREHOUSE MODULE ---> updateWarehouse() ERROR: {}", username, uuid, result.getAllErrors());
                MessageModel warehouses = service.findAllWarehouse(pageable, username, uuid.toString());
                warehouses.setUuid(uuid.toString());
                warehouses.setMessage(MessageCatalog.VALIDATION_ERROR);
                warehouses.setIsError(true);
                model.addAttribute(RESULT, warehouses);
                model.addAttribute("data", warehouse);
                model.addAttribute("action", "update");
                model.addAttribute(PAGE_SIZE, pageable.getPageSize());
                model.addAttribute(WAREHOUSER_LIST, serviceUser.listWarehousers());
                model.addAttribute(INVOICER_LIST, serviceUser.listInvoicers());

                return WAREHOUSE_REDIRECT;
            }
            for(UserModel userModel : users){
                warehouse.getWarehouser().setId(userModel.getId());
            }
            for(UserModel userModel : users){
                warehouse.getInvoicer().setId(userModel.getId());
            }

            MessageModel warehouses = service.updateWarehouse(warehouse, username, uuid.toString());


            warehouses.setUuid(uuid.toString());
            logger.info("[USER : {}] || [UUID : {}] ---> WAREHOUSE MODULE ---> updateWarehouse() RESPONSE: {}", username, uuid, warehouses.getMessage());
            if(warehouses.getIsError()) {
                model.addAttribute(RESULT, warehouses);
                return ERROR_PAGE_500;
            }

            redirectAttributes.addFlashAttribute(PAGE_SIZE, pageable.getPageSize());
            redirectAttributes.addFlashAttribute("resultAction", warehouses);
            logService.saveLog("UPDATING WAREHOUSE", warehouse.getId(), username, uuid.toString());
            return "redirect:/warehouse/list";
        }catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> WAREHOUSE MODULE ---> updateWarehouse() ERROR: {}", username, uuid, exception.getMessage());
            MessageModel message = new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
            message.setUuid(uuid.toString());
            model.addAttribute(RESULT, message);
            return ERROR_PAGE_500;
        }
    }
}
