package mx.edu.utez.warehouse.entry.controller;

import jakarta.servlet.http.HttpSession;
import mx.edu.utez.warehouse.area.controller.AreaController;
import mx.edu.utez.warehouse.entry.model.EntryModel;
import mx.edu.utez.warehouse.entry.service.EntryServiceImpl;
import mx.edu.utez.warehouse.message.model.MessageModel;
import mx.edu.utez.warehouse.product.model.ProductModel;
import mx.edu.utez.warehouse.product.service.ProductServiceImpl;
import mx.edu.utez.warehouse.security.config.SecurityUser;
import mx.edu.utez.warehouse.supplier.service.SupplierServiceImpl;
import mx.edu.utez.warehouse.utils.MessageCatalog;
import mx.edu.utez.warehouse.warehouse.service.WarehouseServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@Controller
@RequestMapping("/entry")
public class EntryController {
    private static final String SPRING_SECURITY_CONTEXT = "SPRING_SECURITY_CONTEXT";
    private static final Logger logger = LogManager.getLogger(AreaController.class);
    @Autowired
    EntryServiceImpl service;
    @Autowired
    SupplierServiceImpl supplierService;

    @Autowired
    WarehouseServiceImpl warehouseService;

    @Autowired
    ProductServiceImpl productService;

    @GetMapping("/list")
    public String findAllEntries(Model model, HttpSession httpSession, Pageable pageable, @ModelAttribute("entry") EntryModel entry) {
        UUID uuid = UUID.randomUUID();
        String username = "";
        try {
            SecurityContextImpl securityContext = (SecurityContextImpl) httpSession.getAttribute(SPRING_SECURITY_CONTEXT);
            SecurityUser user = (SecurityUser) securityContext.getAuthentication().getPrincipal();
            username = user.getUsername();
            logger.info("[USER : {}] || [UUID : {}] ---> EXECUTING ENTRY MODULE ---> findAllEntries()", username, uuid);
            MessageModel entries = service.findAllEntries(pageable, username, uuid.toString());
            logger.info("[USER : {}] || [UUID : {}] ---> ENTRY MODULE ---> findAllEntries() RESPONSE: {}", username, uuid, entries.getMessage());
            entries.setUuid(uuid.toString());

            model.addAttribute("result", entries);
            model.addAttribute("listSupplies", supplierService.findSupplies());
            model.addAttribute("listWarehouses", warehouseService.findWarehouses());
            model.addAttribute("listProducts", productService.findProductsByType(1));
            if (entries.getIsError()) {
                return "errorPages/500";
            }
            model.addAttribute("pageSize", pageable.getPageSize());
            return "entry/entry";
        } catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> ENTRY MODULE ---> findAllEntries() ERROR: {}", username, uuid, exception.getMessage());
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

            logger.info("[USER : {}] || [UUID : {}] ---> EXECUTING ENTRY MODULE ---> findById()", username, uuid);
            MessageModel entry = service.findById(id, username, uuid.toString());
            entry.setUuid(uuid.toString());
            logger.info("[USER : {}] || [UUID : {}] ---> ENTRY MODULE ---> findById() RESPONSE: {}", username, uuid, entry.getData() == null ? "null" : entry.getData().toString());

            return entry;
        }catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> ENTRY MODULE ---> findById() ERROR: {}", username, uuid, exception.getMessage());
            MessageModel message = new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
            message.setUuid(uuid.toString());
            return message;
        }
    }

    @GetMapping("/product/{type}")
    @ResponseBody
    public List<ProductModel> findProductsByType(HttpSession httpSession, @PathVariable("type") Integer type) {
        UUID uuid = UUID.randomUUID();
        String username = "";
        try {
            SecurityContextImpl securityContext = (SecurityContextImpl) httpSession.getAttribute(SPRING_SECURITY_CONTEXT);
            SecurityUser user = (SecurityUser) securityContext.getAuthentication().getPrincipal();
            username = user.getUsername();

            logger.info("[USER : {}] || [UUID : {}] ---> EXECUTING ENTRY MODULE ---> findProductsByType()", username, uuid);
            List<ProductModel> products = productService.findProductsByType(type);
            logger.info("[USER : {}] || [UUID : {}] ---> ENTRY MODULE ---> findProductsByType() RESPONSE: {}", username, uuid, products == null ? "null" : products.toString());

            return products;
        }catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> ENTRY MODULE ---> findProductsByType() ERROR: {}", username, uuid, exception.getMessage());
            MessageModel message = new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
            message.setUuid(uuid.toString());
            return null;
        }
    }


}
