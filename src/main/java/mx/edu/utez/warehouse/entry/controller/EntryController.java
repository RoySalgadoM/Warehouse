package mx.edu.utez.warehouse.entry.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import mx.edu.utez.warehouse.entry.model.EntryModel;
import mx.edu.utez.warehouse.entry.service.EntryServiceImpl;
import mx.edu.utez.warehouse.log.service.LogServiceImpl;
import mx.edu.utez.warehouse.message.model.MessageModel;
import mx.edu.utez.warehouse.product.model.ProductDTO;
import mx.edu.utez.warehouse.product.model.ProductModel;
import mx.edu.utez.warehouse.product.service.ProductRepository;
import mx.edu.utez.warehouse.product.service.ProductServiceImpl;
import mx.edu.utez.warehouse.requisition.model.RequisitionModel;
import mx.edu.utez.warehouse.requisition.model.RequisitionProductModel;
import mx.edu.utez.warehouse.requisition.service.RequisitionProductRepository;
import mx.edu.utez.warehouse.requisition.service.RequisitionRepository;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;


@Controller
@RequestMapping("/entry")
public class EntryController {
    private static final String SPRING_SECURITY_CONTEXT = "SPRING_SECURITY_CONTEXT";
    private static final Logger logger = LogManager.getLogger(EntryController.class);
    private static final String ACTION = "action";
    private static final String RESULT = "result";
    private static final String ERROR_500 = "errorPages/500";
    private static final String PAGE_SIZE = "pageSize";
    private static final String ENTRY_REDIRECT = "entry/entry";
    private static final String RESULT_ACTION = "resultAction";
    private static final String ENTRY_REDIRECT_LIST = "redirect:/entry/list";

    @Autowired
    EntryServiceImpl service;
    @Autowired
    SupplierServiceImpl supplierService;
    @Autowired
    WarehouseServiceImpl warehouseService;
    @Autowired
    ProductServiceImpl productService;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    RequisitionProductRepository requisitionProductRepository;
    @Autowired
    RequisitionRepository requisitionRepository;
    @Autowired
    LogServiceImpl logService;

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
                return ERROR_500;
            }
            model.addAttribute(PAGE_SIZE, pageable.getPageSize());
            return ENTRY_REDIRECT;
        } catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> ENTRY MODULE ---> findAllEntries() ERROR: {}", username, uuid, exception.getMessage());
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

    @PostMapping("/save")
    public String saveEntry(@Valid @ModelAttribute("entry") EntryModel entry, @RequestParam("products") String products, BindingResult result, Model model, RedirectAttributes redirectAttributes, HttpSession httpSession, Pageable pageable) throws JsonProcessingException {
        UUID uuid = UUID.randomUUID();
        String username = "";
        try {
            SecurityContextImpl securityContext = (SecurityContextImpl) httpSession.getAttribute(SPRING_SECURITY_CONTEXT);
            SecurityUser user = (SecurityUser) securityContext.getAuthentication().getPrincipal();
            username = user.getUsername();
            logger.info("[USER : {}] || [UUID : {}] ---> EXECUTING ENTRY MODULE ---> saveEntry()", username, uuid);
            model.addAttribute(ACTION, "save");
            model.addAttribute(PAGE_SIZE, pageable.getPageSize());

            if(service.isExistEntry(entry.getRequisition().getCode())){
                result.rejectValue("requisition.code", "entry.requisition.code", "El código ya existe");
            }

            if(products.isEmpty()){
                result.rejectValue("requisition.requisitionProductModels", "entry.requisition.requisitionProductModels", "La entrada debe tener al menos un producto");
            }

            if(result.hasErrors()) {
                logger.error("[USER : {}] || [UUID : {}] ---> ENTRY MODULE ---> saveEntry() ERROR: {}", username, uuid, result.getAllErrors());
                MessageModel entries = service.findAllEntries(pageable, username, uuid.toString());
                entries.setUuid(uuid.toString());
                entries.setMessage(MessageCatalog.VALIDATION_ERROR);
                entries.setIsError(true);
                model.addAttribute(RESULT, entries);
                model.addAttribute("data", entry);
                return ENTRY_REDIRECT;
            }
            ObjectMapper mapper = new ObjectMapper();
            List<ProductDTO> listProducts = mapper.readValue(products, new TypeReference<List<ProductDTO>>(){});
            Double total = 0.0;
            Integer quantity = 0;
            for (ProductDTO product : listProducts) {
                if(product.getQuantity() == null || product.getQuantity() <= 0){
                    result.rejectValue("requisition.requisitionProductModels", "entry.requisition.requisitionProductModels", "Las cantidades de los productos deben ser mayor a 0");
                }
                total += product.getQuantity() * product.getUnitPrice();
                quantity += product.getQuantity();
            }
            entry.getRequisition().setTotalAmount(total);
            entry.getRequisition().setTotalOfProducts(quantity);

            MessageModel entries = service.registerEntry(entry, username, uuid.toString());
            EntryModel entryModel = (EntryModel) entries.getData();
            RequisitionModel order = entryModel.getRequisition();

            for(ProductDTO product : listProducts) {
                ProductModel productModel = productRepository.findProductById(product.getId());
                requisitionProductRepository.save(new RequisitionProductModel(product.getQuantity(), order, productModel));
            }
            entries.setUuid(uuid.toString());
            logger.info("[USER : {}] || [UUID : {}] ---> ENTRY MODULE ---> saveEntry() RESPONSE: {}", username, uuid, entries.getMessage());

            if (entries.getIsError()){
                model.addAttribute(RESULT, entries);
                return ERROR_500;
            }
            redirectAttributes.addFlashAttribute(RESULT_ACTION, entries);
            logService.saveLog("SAVING NEW ENTRY", 0L, username, uuid.toString());
            return ENTRY_REDIRECT_LIST;
        }catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> ENTRY MODULE ---> saveEntry() ERROR: {}", username, uuid, exception.getMessage());
            MessageModel message = new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
            message.setUuid(uuid.toString());
            model.addAttribute(RESULT, message);
            return ERROR_500;
        }
    }

    @PostMapping("/cancel")
    public String cancelEntry(Model model, @RequestParam long id, RedirectAttributes redirectAttributes, HttpSession httpSession) {
        UUID uuid = UUID.randomUUID();
        String username = "";
        try {
            SecurityContextImpl securityContext = (SecurityContextImpl) httpSession.getAttribute(SPRING_SECURITY_CONTEXT);
            SecurityUser user = (SecurityUser) securityContext.getAuthentication().getPrincipal();
            username = user.getUsername();
            logger.info("[USER : {}] || [UUID : {}] ---> EXECUTING ENTRY MODULE ---> cancelEntry()", username, uuid);
            MessageModel entries = service.cancelEntry(id, username, uuid.toString());
            logger.info("[USER : {}] || [UUID : {}] ---> ENTRY MODULE ---> cancelEntry() RESPONSE: {}", username, uuid, entries);
            entries.setUuid(uuid.toString());

            if(entries.getIsError()) {
                model.addAttribute(RESULT, entries);
                return ERROR_500;
            }

            redirectAttributes.addFlashAttribute(RESULT_ACTION, entries);
            logService.saveLog("CHANGING(CANCEL) STATUS OF ENTRY", id, username, uuid.toString());
            return ENTRY_REDIRECT_LIST;
        }catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> ENTRY MODULE ---> cancelEntry() ERROR: {}", username, uuid, exception.getMessage());
            MessageModel message = new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
            message.setUuid(uuid.toString());
            model.addAttribute(RESULT, message);
            return ERROR_500;
        }
    }
}
