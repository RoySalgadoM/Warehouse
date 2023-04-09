package mx.edu.utez.warehouse.product.controller;


import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/product")
public class ProductController {
    private static final String SPRING_SECURITY_CONTEXT = "SPRING_SECURITY_CONTEXT";
    private static final Logger logger = LogManager.getLogger(ProductController.class);

    @Autowired
    private ProductService service;
    @GetMapping("/new")
    public String newMethod(Model model){
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        Integer product = 0;
        model.addAttribute("list" , list);
        model.addAttribute("product", product);

        return "product/AAA";
    }

    @PostMapping("/newProd")
    public String newProd(@RequestParam("products") String products){
        List<Integer> bindProd = new ArrayList<>(Arrays.asList(products.split(",")).stream().map(Integer::parseInt).collect(Collectors.toList()));
        System.out.println(products);
        return "product/AAA";
    }


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

            model.addAttribute("result", products);
            if (products.getIsError()) {
                return "errorPages/500";
            }
            List<Integer> list = new ArrayList<>();

            model.addAttribute("pageSize", pageable.getPageSize());
            model.addAttribute("list", list);
            return "product/product";
        } catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> PRODUCT MODULE ---> findAllProducts() ERROR: {}", username, uuid, exception.getMessage());
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

            logger.info("[USER : {}] || [UUID : {}] ---> EXECUTING PRODUCT MODULE ---> findById()", username, uuid);
            MessageModel product = service.findById(id, username, uuid.toString());
            product.setUuid(uuid.toString());
            logger.info("[USER : {}] || [UUID : {}] ---> PRODUCT MODULE ---> findById() RESPONSE: {}", username, uuid, product.getData() == null ? "null" : product.getData().toString());

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
            model.addAttribute("pageSize", pageable.getPageSize());

            if(result.hasErrors()) {
                logger.error("[USER : {}] || [UUID : {}] ---> PRODUCT MODULE ---> saveProduct() ERROR: {}", username, uuid, result.getAllErrors());
                MessageModel products = service.findAllProducts(pageable, username, uuid.toString());
                products.setUuid(uuid.toString());
                products.setMessage(MessageCatalog.VALIDATION_ERROR);
                products.setIsError(true);
                model.addAttribute("result", products);
                model.addAttribute("data", product);

                return "product/product";
            }

            MessageModel products = service.registerProduct(product, username, uuid.toString());
            products.setUuid(uuid.toString());
            logger.info("[USER : {}] || [UUID : {}] ---> PRODUCT MODULE ---> saveProduct() RESPONSE: {}", username, uuid, products.getMessage());

            if (products.getIsError()){
                model.addAttribute("result", products);
                return "errorPages/500";
            }
            redirectAttributes.addFlashAttribute("resultAction", products);
            return "redirect:/product/list";
        }catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> PRODUCT MODULE ---> saveProduct() ERROR: {}", username, uuid, exception.getMessage());
            MessageModel message = new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
            message.setUuid(uuid.toString());
            model.addAttribute("result", message);
            return "errorPages/500";
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

            if(result.hasErrors()) {
                logger.error("[USER : {}] || [UUID : {}] ---> PRODUCT MODULE ---> updateProduct() ERROR: {}", username, uuid, result.getAllErrors());
                MessageModel products = service.findAllProducts(pageable, username, uuid.toString());
                products.setUuid(uuid.toString());
                products.setMessage(MessageCatalog.VALIDATION_ERROR);
                products.setIsError(true);
                model.addAttribute("result", products);
                model.addAttribute("data", product);
                model.addAttribute("action", "update");
                model.addAttribute("pageSize", pageable.getPageSize());

                return "product/product";
            }
            MessageModel products = service.updateProduct(product, username, uuid.toString());
            products.setUuid(uuid.toString());

            logger.info("[USER : {}] || [UUID : {}] ---> PRODUCT MODULE ---> updateProduct() RESPONSE: {}", username, uuid, products.getMessage());
            if(products.getIsError()) {
                model.addAttribute("result", products);
                return "errorPages/500";
            }

            redirectAttributes.addFlashAttribute("pageSize", pageable.getPageSize());
            redirectAttributes.addFlashAttribute("resultAction", products);
            return "redirect:/product/list";
        }catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> PRODUCT MODULE ---> updateProduct() ERROR: {}", username, uuid, exception.getMessage());
            MessageModel message = new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
            message.setUuid(uuid.toString());
            model.addAttribute("result", message);
            return "errorPages/500";
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
                model.addAttribute("result", products);
                return "errorPages/500";
            }

            redirectAttributes.addFlashAttribute("resultAction", products);
            return "redirect:/product/list";
        }catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> PRODUCT MODULE ---> disableProduct() ERROR: {}", username, uuid, exception.getMessage());
            MessageModel message = new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
            message.setUuid(uuid.toString());
            model.addAttribute("result", message);
            return "errorPages/500";
        }
    }

}
