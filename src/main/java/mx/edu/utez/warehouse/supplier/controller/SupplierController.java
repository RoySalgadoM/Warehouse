package mx.edu.utez.warehouse.supplier.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
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
    private static final Logger logger = LogManager.getLogger(SupplierController.class);
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

            model.addAttribute("result", supplies);
            if (supplies.getIsError()) {
                return "errorPages/500";
            }

            model.addAttribute("pageSize", pageable.getPageSize());
            return "supplier/supplier";
        } catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> SUPPLIER MODULE ---> findAllSupplies() ERROR: {}", username, uuid, exception.getMessage());
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

            logger.info("[USER : {}] || [UUID : {}] ---> EXECUTING SUPPLIER MODULE ---> findById()", username, uuid);
            MessageModel supplier = service.findById(id, username, uuid.toString());
            supplier.setUuid(uuid.toString());
            logger.info("[USER : {}] || [UUID : {}] ---> SUPPLIER MODULE ---> findById() RESPONSE: {}", username, uuid, supplier.getData() == null ? "null" : supplier.getData().toString());

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
            model.addAttribute("pageSize", pageable.getPageSize());

            if(service.isExistSupplier(supplier.getName())){
                result.rejectValue("name", "supplier.name", "El nombre ya existe");
            }
            if(result.hasErrors()) {
                logger.error("[USER : {}] || [UUID : {}] ---> SUPPLIER MODULE ---> saveSupplier() ERROR: {}", username, uuid, result.getAllErrors());
                MessageModel supplies = service.findAllSupplies(pageable, username, uuid.toString());
                supplies.setUuid(uuid.toString());
                supplies.setMessage(MessageCatalog.VALIDATION_ERROR);
                supplies.setIsError(true);
                model.addAttribute("result", supplies);
                model.addAttribute("data", supplier);

                return "supplier/supplier";
            }
            MessageModel supplies = service.registerSupplier(supplier, username, uuid.toString());
            supplies.setUuid(uuid.toString());
            logger.info("[USER : {}] || [UUID : {}] ---> SUPPLIER MODULE ---> saveSupplier() RESPONSE: {}", username, uuid, supplies.getMessage());

            if (supplies.getIsError()){
                model.addAttribute("result", supplies);
                return "errorPages/500";
            }
            redirectAttributes.addFlashAttribute("resultAction", supplies);
            return "redirect:/supplier/list";
        }catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> SUPPLIER MODULE ---> saveSupplier() ERROR: {}", username, uuid, exception.getMessage());
            MessageModel message = new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
            message.setUuid(uuid.toString());
            model.addAttribute("result", message);
            return "errorPages/500";
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

            if(service.isExistSupplierAndId(supplier.getName(), supplier.getId())){
                result.rejectValue("name", "supplier.name", "El nombre ya existe");
            }
            if(result.hasErrors()) {
                logger.error("[USER : {}] || [UUID : {}] ---> SUPPLIER MODULE ---> updateSupplier() ERROR: {}", username, uuid, result.getAllErrors());
                MessageModel supplies = service.findAllSupplies(pageable, username, uuid.toString());
                supplies.setUuid(uuid.toString());
                supplies.setMessage(MessageCatalog.VALIDATION_ERROR);
                supplies.setIsError(true);
                model.addAttribute("result", supplies);
                model.addAttribute("data", supplies);
                model.addAttribute("action", "update");
                model.addAttribute("pageSize", pageable.getPageSize());

                return "supplier/supplier";
            }
            MessageModel supplies = service.updateSupplier(supplier, username, uuid.toString());
            supplies.setUuid(uuid.toString());

            logger.info("[USER : {}] || [UUID : {}] ---> SUPPLIER MODULE ---> updateSupplier() RESPONSE: {}", username, uuid, supplies.getMessage());
            if(supplies.getIsError()) {
                model.addAttribute("result", supplies);
                return "errorPages/500";
            }

            redirectAttributes.addFlashAttribute("pageSize", pageable.getPageSize());
            redirectAttributes.addFlashAttribute("resultAction", supplies);
            return "redirect:/supplier/list";
        }catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> SUPPLIER MODULE ---> updateSupplier() ERROR: {}", username, uuid, exception.getMessage());
            MessageModel message = new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
            message.setUuid(uuid.toString());
            model.addAttribute("result", message);
            return "errorPages/500";
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
                model.addAttribute("result", supplies);
                return "errorPages/500";
            }

            redirectAttributes.addFlashAttribute("resultAction", supplies);
            return "redirect:/supplier/list";
        }catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> SUPPLIER MODULE ---> disableSupplier() ERROR: {}", username, uuid, exception.getMessage());
            MessageModel message = new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
            message.setUuid(uuid.toString());
            model.addAttribute("result", message);
            return "errorPages/500";
        }
    }

}
