package mx.edu.utez.warehouse.supplier.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import mx.edu.utez.warehouse.log.service.LogServiceImpl;
import mx.edu.utez.warehouse.message.model.MessageModel;
import mx.edu.utez.warehouse.security.config.SecurityUser;
import mx.edu.utez.warehouse.supplier.model.SupplierModel;
import mx.edu.utez.warehouse.supplier.service.SupplierServiceImpl;
import mx.edu.utez.warehouse.utils.MessageCatalog;
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

import java.util.UUID;

@Controller
@RequestMapping("/supplier")
public class SupplierController {
    private static final String SPRING_SECURITY_CONTEXT = "SPRING_SECURITY_CONTEXT";
    private static final String ERROR_500 = "errorPages/500";
    private static final Logger logger = LogManager.getLogger(SupplierController.class);
    private static final String RESULT = "result";
    private static final String RESULT_ACTION = "resultAction";
    private static final String PAGE_SIZE = "pageSize";
    private static final String SUPPLIER_REDIRECT = "supplier/supplier";

    private static final String SUPPLIER_REDIRECT_LIST = "redirect:/supplier/list";
    @Autowired
    LogServiceImpl logService;

    @Autowired
    SupplierServiceImpl service;

    @GetMapping("/list")
    public String findAllSupplies(Model model, HttpSession httpSession, Pageable pageable, @ModelAttribute("supplier") SupplierModel supplier) {
        UUID uuid = UUID.randomUUID();
        String username = "";
        try {
            SecurityContextImpl securityContext = (SecurityContextImpl) httpSession.getAttribute(SPRING_SECURITY_CONTEXT);
            SecurityUser user = (SecurityUser) securityContext.getAuthentication().getPrincipal();
            username = user.getUsername();
            logger.info("[USER : {}] || [UUID : {}] ---> EXECUTING SUPPLIER MODULE ---> findAllSupplies()", username, uuid);
            MessageModel supplies = service.findAllSupplies(pageable, username, uuid.toString());
            logger.info("[USER : {}] || [UUID : {}] ---> AREA MODULE ---> findAllSupplies() RESPONSE: {}", username, uuid, supplies.getMessage());
            supplies.setUuid(uuid.toString());

            model.addAttribute(RESULT, supplies);
            if (supplies.getIsError()) {
                return ERROR_500;
            }

            model.addAttribute(PAGE_SIZE, pageable.getPageSize());
            logService.saveLog("FINDING SUPPLIERS", 0L, username, uuid.toString());

            return SUPPLIER_REDIRECT;
        } catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> SUPPLIER MODULE ---> findAllSupplies() ERROR: {}", username, uuid, exception.getMessage());
            MessageModel message = new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
            message.setUuid(uuid.toString());
            model.addAttribute(RESULT, message);
            return ERROR_500;
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

            logger.info("[USER : {}] || [UUID : {}] ---> EXECUTING SUPPLIER MODULE ---> findById()", username, uuid);
            MessageModel supplier = service.findById(id, username, uuid.toString());
            supplier.setUuid(uuid.toString());
            logger.info("[USER : {}] || [UUID : {}] ---> SUPPLIER MODULE ---> findById() RESPONSE: {}", username, uuid, supplier.getData() == null ? "null" : supplier.getData().toString());
            logService.saveLog("FINDING SUPPLIER BY ID", id, username, uuid.toString());
            return supplier;
        }catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> SUPPLIER MODULE ---> findById() ERROR: {}", username, uuid, exception.getMessage());
            MessageModel message = new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
            message.setUuid(uuid.toString());
            return message;
        }
    }

    @PostMapping("/save")
    public String saveSupplier(@Valid @ModelAttribute("supplier") SupplierModel supplier, BindingResult result, Model model, RedirectAttributes redirectAttributes, HttpSession httpSession, Pageable pageable) {
        UUID uuid = UUID.randomUUID();
        String username = "";
        try {
            SecurityContextImpl securityContext = (SecurityContextImpl) httpSession.getAttribute(SPRING_SECURITY_CONTEXT);
            SecurityUser user = (SecurityUser) securityContext.getAuthentication().getPrincipal();
            username = user.getUsername();
            logger.info("[USER : {}] || [UUID : {}] ---> EXECUTING SUPPLIER MODULE ---> saveSupplier()", username, uuid);
            model.addAttribute("action", "save");
            model.addAttribute(PAGE_SIZE, pageable.getPageSize());

            if(service.isExistSupplierByEmail(supplier.getEmail())){
                result.rejectValue("email", "supplier.email", "El email ya existe");
            }
            if (service.isExistSupplierByRfc(supplier.getRfc())) {
                result.rejectValue("rfc", "supplier.rfc", "El RFC ya existe");
            }

            if(result.hasErrors()) {
                logger.error("[USER : {}] || [UUID : {}] ---> SUPPLIER MODULE ---> saveSupplier() ERROR: {}", username, uuid, result.getAllErrors());
                MessageModel supplies = service.findAllSupplies(pageable, username, uuid.toString());
                supplies.setUuid(uuid.toString());
                supplies.setMessage(MessageCatalog.VALIDATION_ERROR);
                supplies.setIsError(true);
                model.addAttribute(RESULT, supplies);
                model.addAttribute("data", supplier);

                return SUPPLIER_REDIRECT;
            }
            MessageModel supplies = service.registerSupplier(supplier, username, uuid.toString());
            supplies.setUuid(uuid.toString());
            logger.info("[USER : {}] || [UUID : {}] ---> SUPPLIER MODULE ---> saveSupplier() RESPONSE: {}", username, uuid, supplies.getMessage());

            if (supplies.getIsError()){
                model.addAttribute(RESULT, supplies);
                return ERROR_500;
            }
            redirectAttributes.addFlashAttribute(RESULT_ACTION, supplies);
            logService.saveLog("SAVING NEW SUPPLIER", 0L, username, uuid.toString());
            return SUPPLIER_REDIRECT_LIST;
        }catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> SUPPLIER MODULE ---> saveSupplier() ERROR: {}", username, uuid, exception.getMessage());
            MessageModel message = new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
            message.setUuid(uuid.toString());
            model.addAttribute(RESULT, message);
            return ERROR_500;
        }
    }

    @PostMapping("/update")
    public String updateSupplier(@Valid @ModelAttribute("supplier") SupplierModel supplier, BindingResult result, Model model, RedirectAttributes redirectAttributes, HttpSession httpSession, Pageable pageable) {
        UUID uuid = UUID.randomUUID();
        String username = "";
        try {
            SecurityContextImpl securityContext = (SecurityContextImpl) httpSession.getAttribute(SPRING_SECURITY_CONTEXT);
            SecurityUser user = (SecurityUser) securityContext.getAuthentication().getPrincipal();
            username = user.getUsername();
            logger.info("[USER : {}] || [UUID : {}] ---> EXECUTING SUPPLIER MODULE ---> updateSupplier()", username, uuid);

            if(service.isExistSupplierByEmailAndId(supplier.getEmail(), supplier.getId())){
                result.rejectValue("email", "supplier.email", "El correo ya existe");
            }
            if (service.isExistSupplierByRfcAndId(supplier.getRfc(), supplier.getId())) {
                result.rejectValue("rfc", "supplier.rfc", "El RFC ya existe");
            }

            if(result.hasErrors()) {
                logger.error("[USER : {}] || [UUID : {}] ---> SUPPLIER MODULE ---> updateSupplier() ERROR: {}", username, uuid, result.getAllErrors());
                MessageModel supplies = service.findAllSupplies(pageable, username, uuid.toString());
                supplies.setUuid(uuid.toString());
                supplies.setMessage(MessageCatalog.VALIDATION_ERROR);
                supplies.setIsError(true);
                model.addAttribute(RESULT, supplies);
                model.addAttribute("data", supplier);
                model.addAttribute("action", "update");
                model.addAttribute(PAGE_SIZE, pageable.getPageSize());

                return SUPPLIER_REDIRECT;
            }
            MessageModel supplies = service.updateSupplier(supplier, username, uuid.toString());
            supplies.setUuid(uuid.toString());

            logger.info("[USER : {}] || [UUID : {}] ---> SUPPLIER MODULE ---> updateSupplier() RESPONSE: {}", username, uuid, supplies.getMessage());
            if(supplies.getIsError()) {
                model.addAttribute(RESULT, supplies);
                return ERROR_500;
            }

            redirectAttributes.addFlashAttribute(PAGE_SIZE, pageable.getPageSize());
            redirectAttributes.addFlashAttribute(RESULT_ACTION, supplies);
            logService.saveLog("UPDATING SUPPLIER", supplier.getId(), username, uuid.toString());
            return SUPPLIER_REDIRECT_LIST;
        }catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> SUPPLIER MODULE ---> updateSupplier() ERROR: {}", username, uuid, exception.getMessage());
            MessageModel message = new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
            message.setUuid(uuid.toString());
            model.addAttribute(RESULT, message);
            return ERROR_500;
        }
    }

    @PostMapping("/disable")
    public String disableSupplier(Model model, @RequestParam long id, RedirectAttributes redirectAttributes, HttpSession httpSession) {
        UUID uuid = UUID.randomUUID();
        String username = "";
        try {
            SecurityContextImpl securityContext = (SecurityContextImpl) httpSession.getAttribute(SPRING_SECURITY_CONTEXT);
            SecurityUser user = (SecurityUser) securityContext.getAuthentication().getPrincipal();
            username = user.getUsername();
            logger.info("[USER : {}] || [UUID : {}] ---> EXECUTING AREA MODULE ---> disableSupplier()", username, uuid);
            MessageModel supplies = service.disableSupplier(id, username, uuid.toString());
            logger.info("[USER : {}] || [UUID : {}] ---> SUPPLIER MODULE ---> disableSupplier() RESPONSE: {}", username, uuid, supplies);
            supplies.setUuid(uuid.toString());

            if(supplies.getIsError()) {
                model.addAttribute(RESULT, supplies);
                return ERROR_500;
            }

            redirectAttributes.addFlashAttribute(RESULT_ACTION, supplies);
            logService.saveLog("DISABLE SUPPLIER", id, username, uuid.toString());
            return SUPPLIER_REDIRECT_LIST;
        }catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> SUPPLIER MODULE ---> disableSupplier() ERROR: {}", username, uuid, exception.getMessage());
            MessageModel message = new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
            message.setUuid(uuid.toString());
            model.addAttribute(RESULT, message);
            return ERROR_500;
        }
    }

}
