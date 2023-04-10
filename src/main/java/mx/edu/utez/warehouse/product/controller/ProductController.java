package mx.edu.utez.warehouse.product.controller;


import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import mx.edu.utez.warehouse.log.service.LogServiceImpl;
import mx.edu.utez.warehouse.message.model.MessageModel;
import mx.edu.utez.warehouse.product.model.ProductModel;
import mx.edu.utez.warehouse.product.service.ProductService;
import mx.edu.utez.warehouse.security.config.SecurityUser;
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
import org.thymeleaf.util.ArrayUtils;

import java.util.UUID;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/product")
public class ProductController {
    private static final String SPRING_SECURITY_CONTEXT = "SPRING_SECURITY_CONTEXT";
    private static final String RESULT = "result";
    private static final String ERROR_500 = "errorPages/500";
    private static final String PAGE_SIZE = "pageSize";
    private static final String PRODUCT_REDIRECT = "product/product";
    private static final String UNIT_NOT_VALID = "El tipo de unidad no es válido";
    private static final String PRODUCT_VALUE_CONSTANT = "product.unit";
    private static final String RESULT_ACTION = "resultAction";
    private static final String PRODUCT_REDIRECT_LIST = "redirect:/product/list";
    private static final Logger logger = LogManager.getLogger(ProductController.class);

    @Autowired
    LogServiceImpl logService;
    @Autowired
    private ProductService service;

    @GetMapping("/list")
    public String findAllProducts(Model model, HttpSession httpSession, Pageable pageable, @ModelAttribute("product") ProductModel product){
        UUID uuid = UUID.randomUUID();
        String username = "";
        try {
            SecurityContextImpl securityContext = (SecurityContextImpl) httpSession.getAttribute(SPRING_SECURITY_CONTEXT);
            SecurityUser user = (SecurityUser) securityContext.getAuthentication().getPrincipal();
            username = user.getUsername();
            logger.info("[USER : {}] || [UUID : {}] ---> EXECUTING PRODUCT MODULE ---> findAllProducts()", username, uuid);
            MessageModel products = service.findAllProducts(pageable, username, uuid.toString());
            logger.info("[USER : {}] || [UUID : {}] ---> PRODUCT MODULE ---> findAllProducts() RESPONSE: {}", username, uuid, products.getMessage());
            products.setUuid(uuid.toString());

            model.addAttribute(RESULT, products);
            if (products.getIsError()) {
                return ERROR_500;
            }

            model.addAttribute(PAGE_SIZE, pageable.getPageSize());
            logService.saveLog("FINDING PRODUCTS", 0L, username, uuid.toString());

            return PRODUCT_REDIRECT;
        } catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> PRODUCT MODULE ---> findAllProducts() ERROR: {}", username, uuid, exception.getMessage());
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

            logger.info("[USER : {}] || [UUID : {}] ---> EXECUTING PRODUCT MODULE ---> findById()", username, uuid);
            MessageModel product = service.findById(id, username, uuid.toString());
            product.setUuid(uuid.toString());
            logger.info("[USER : {}] || [UUID : {}] ---> PRODUCT MODULE ---> findById() RESPONSE: {}", username, uuid, product.getData() == null ? "null" : product.getData().toString());
            logService.saveLog("FINDING PRODUCT BY ID", id, username, uuid.toString());
            return product;
        }catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> PRODUCT MODULE ---> findById() ERROR: {}", username, uuid, exception.getMessage());
            MessageModel message = new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
            message.setUuid(uuid.toString());
            return message;
        }
    }
    @PostMapping("/save")
    public String saveProduct(@Valid @ModelAttribute("product") ProductModel product, BindingResult result, Model model, RedirectAttributes redirectAttributes, HttpSession httpSession, Pageable pageable) {
        UUID uuid = UUID.randomUUID();
        String username = "";
        try {
            SecurityContextImpl securityContext = (SecurityContextImpl) httpSession.getAttribute(SPRING_SECURITY_CONTEXT);
            SecurityUser user = (SecurityUser) securityContext.getAuthentication().getPrincipal();
            username = user.getUsername();
            logger.info("[USER : {}] || [UUID : {}] ---> EXECUTING PRODUCT MODULE ---> saveProduct()", username, uuid);
            model.addAttribute("action", "save");
            model.addAttribute(PAGE_SIZE, pageable.getPageSize());
            if(product.getType() == 1){
                product.setSerialNumber(null);
            }
            if(product.getType() == 2){
                product.setExpirationDate(null);
                product.getSerialNumber();
                if(!(Pattern.matches("[A-Za-z0-9 ]+", product.getSerialNumber()))){
                    result.rejectValue("serialNumber", "product.serialNumber", "El número serial debe ser alfanumérico");
                }
            }
            if (product.getUnitPrice() == null){
                result.rejectValue("unitPrice", "product.unitPrice", "El precio unitario no puede estar vacío");
            }

            if (!ArrayUtils.contains(new String[]{"Piezas", "Cajas", "Unidades", "Toneladas", "Kilos", "Gramos", "Litros", "Metros", "Centímetros"}, product.getUnit())){
                result.rejectValue("unit", PRODUCT_VALUE_CONSTANT, UNIT_NOT_VALID);
            }
            if (!ArrayUtils.contains(new Integer[]{1, 2}, product.getType())){
                result.rejectValue("unit", PRODUCT_VALUE_CONSTANT, UNIT_NOT_VALID);
            }
            if(result.hasErrors()) {
                logger.error("[USER : {}] || [UUID : {}] ---> PRODUCT MODULE ---> saveProduct() ERROR: {}", username, uuid, result.getAllErrors());
                MessageModel products = service.findAllProducts(pageable, username, uuid.toString());
                products.setUuid(uuid.toString());
                products.setMessage(MessageCatalog.VALIDATION_ERROR);
                products.setIsError(true);
                model.addAttribute(RESULT, products);
                model.addAttribute("data", product);

                return PRODUCT_REDIRECT;
            }

            MessageModel products = service.registerProduct(product, username, uuid.toString());
            products.setUuid(uuid.toString());
            logger.info("[USER : {}] || [UUID : {}] ---> PRODUCT MODULE ---> saveProduct() RESPONSE: {}", username, uuid, products.getMessage());

            if (products.getIsError()){
                model.addAttribute(RESULT, products);
                return ERROR_500;
            }
            redirectAttributes.addFlashAttribute(RESULT_ACTION, products);
            logService.saveLog("SAVING NEW PRODUCT", 0L, username, uuid.toString());
            return PRODUCT_REDIRECT_LIST;
        }catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> PRODUCT MODULE ---> saveProduct() ERROR: {}", username, uuid, exception.getMessage());
            MessageModel message = new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
            message.setUuid(uuid.toString());
            model.addAttribute(RESULT, message);
            return ERROR_500;
        }
    }

    @PostMapping("/update")
    public String updateProduct(@Valid @ModelAttribute("product") ProductModel product, BindingResult result, Model model, RedirectAttributes redirectAttributes, HttpSession httpSession, Pageable pageable) {
        UUID uuid = UUID.randomUUID();
        String username = "";
        try {
            SecurityContextImpl securityContext = (SecurityContextImpl) httpSession.getAttribute(SPRING_SECURITY_CONTEXT);
            SecurityUser user = (SecurityUser) securityContext.getAuthentication().getPrincipal();
            username = user.getUsername();
            logger.info("[USER : {}] || [UUID : {}] ---> EXECUTING PRODUCT MODULE ---> updateProduct()", username, uuid);
            if(product.getType() == 1){
                product.setSerialNumber(null);
            }
            if(product.getType() == 2){
                product.setExpirationDate(null);
                product.getSerialNumber();
                if(!(Pattern.matches("[A-Za-z0-9 ]+", product.getSerialNumber()))){
                    result.rejectValue("serialNumber", "product.serialNumber", "El número serial debe ser alfanumérico");
                }
            }
            if (product.getUnitPrice() == null){
                result.rejectValue("unitPrice", "product.unitPrice", "El precio unitario no puede estar vacío");
            }

            if (!ArrayUtils.contains(new String[]{"Piezas", "Cajas", "Unidades", "Toneladas", "Kilos", "Gramos", "Litros", "Metros", "Centímetros"}, product.getUnit())){
                result.rejectValue("unit", PRODUCT_VALUE_CONSTANT, UNIT_NOT_VALID);
            }

            if(result.hasErrors()) {
                logger.error("[USER : {}] || [UUID : {}] ---> PRODUCT MODULE ---> updateProduct() ERROR: {}", username, uuid, result.getAllErrors());
                MessageModel products = service.findAllProducts(pageable, username, uuid.toString());
                products.setUuid(uuid.toString());
                products.setMessage(MessageCatalog.VALIDATION_ERROR);
                products.setIsError(true);
                model.addAttribute(RESULT, products);
                model.addAttribute("data", product);
                model.addAttribute("action", "update");
                model.addAttribute(PAGE_SIZE, pageable.getPageSize());

                return PRODUCT_REDIRECT;
            }
            MessageModel products = service.updateProduct(product, username, uuid.toString());
            products.setUuid(uuid.toString());

            logger.info("[USER : {}] || [UUID : {}] ---> PRODUCT MODULE ---> updateProduct() RESPONSE: {}", username, uuid, products.getMessage());
            if(products.getIsError()) {
                model.addAttribute(RESULT, products);
                return ERROR_500;
            }

            redirectAttributes.addFlashAttribute(PAGE_SIZE, pageable.getPageSize());
            redirectAttributes.addFlashAttribute(RESULT_ACTION, products);
            logService.saveLog("UPDATING PRODUCT", product.getId(), username, uuid.toString());
            return PRODUCT_REDIRECT_LIST;
        }catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> PRODUCT MODULE ---> updateProduct() ERROR: {}", username, uuid, exception.getMessage());
            MessageModel message = new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
            message.setUuid(uuid.toString());
            model.addAttribute(RESULT, message);
            return ERROR_500;
        }
    }

    @PostMapping("/disable")
    public String disableProduct(Model model, @RequestParam long id, RedirectAttributes redirectAttributes, HttpSession httpSession) {
        UUID uuid = UUID.randomUUID();
        String username = "";
        try {
            SecurityContextImpl securityContext = (SecurityContextImpl) httpSession.getAttribute(SPRING_SECURITY_CONTEXT);
            SecurityUser user = (SecurityUser) securityContext.getAuthentication().getPrincipal();
            username = user.getUsername();
            logger.info("[USER : {}] || [UUID : {}] ---> EXECUTING PRODUCT MODULE ---> disableProduct()", username, uuid);
            MessageModel products = service.disableProduct(id, username, uuid.toString());
            logger.info("[USER : {}] || [UUID : {}] ---> PRODUCT MODULE ---> disableProduct() RESPONSE: {}", username, uuid, products);
            products.setUuid(uuid.toString());

            if(products.getIsError()) {
                model.addAttribute(RESULT, products);
                return ERROR_500;
            }

            redirectAttributes.addFlashAttribute(RESULT_ACTION, products);
            logService.saveLog("DISABLE PRODUCT", id, username, uuid.toString());
            return PRODUCT_REDIRECT_LIST;
        }catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> PRODUCT MODULE ---> disableProduct() ERROR: {}", username, uuid, exception.getMessage());
            MessageModel message = new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
            message.setUuid(uuid.toString());
            model.addAttribute(RESULT, message);
            return ERROR_500;
        }
    }

}
