package mx.edu.utez.warehouse.output.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import mx.edu.utez.warehouse.area.service.AreaServiceImpl;
import mx.edu.utez.warehouse.entry.controller.EntryController;
import mx.edu.utez.warehouse.entry.model.EntryModel;
import mx.edu.utez.warehouse.entry.service.EntryRepository;
import mx.edu.utez.warehouse.entry.service.EntryServiceImpl;
import mx.edu.utez.warehouse.log.service.LogServiceImpl;
import mx.edu.utez.warehouse.message.model.MessageModel;
import mx.edu.utez.warehouse.output.model.OutputModel;
import mx.edu.utez.warehouse.output.service.OutputRepository;
import mx.edu.utez.warehouse.output.service.OutputServiceImpl;
import mx.edu.utez.warehouse.product.model.ProductDTO;
import mx.edu.utez.warehouse.product.model.ProductModel;
import mx.edu.utez.warehouse.product.model.WarehouseProductModel;
import mx.edu.utez.warehouse.product.service.ProductRepository;
import mx.edu.utez.warehouse.product.service.ProductServiceImpl;
import mx.edu.utez.warehouse.product.service.WarehouseProductRepository;
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
import org.thymeleaf.util.ArrayUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/output")
public class OutputController {
    private static final String SPRING_SECURITY_CONTEXT = "SPRING_SECURITY_CONTEXT";
    private static final Logger logger = LogManager.getLogger(OutputController.class);
    private static final String ACTION = "action";
    private static final String RESULT = "result";
    private static final String ERROR_500 = "errorPages/500";
    private static final String PAGE_SIZE = "pageSize";
    private static final String OUTPUT_REDIRECT = "output/outputs";
    private static final String RESULT_ACTION = "resultAction";
    private static final String OUTPUT_REDIRECT_LIST = "redirect:/output/list";

    @Autowired
    OutputServiceImpl service;
    @Autowired
    OutputRepository repository;
    @Autowired
    AreaServiceImpl areaService;
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
    WarehouseProductRepository warehouseProductRepository;
    @Autowired
    LogServiceImpl logService;
    @GetMapping("/list")
    public String findAllOutputs(Model model, HttpSession httpSession, Pageable pageable, @ModelAttribute("output") OutputModel output) {
        UUID uuid = UUID.randomUUID();
        String username = "";
        try {
            SecurityContextImpl securityContext = (SecurityContextImpl) httpSession.getAttribute(SPRING_SECURITY_CONTEXT);
            SecurityUser user = (SecurityUser) securityContext.getAuthentication().getPrincipal();
            username = user.getUsername();
            logger.info("[USER : {}] || [UUID : {}] ---> EXECUTING OUTPUT MODULE ---> findAllOutputs()", username, uuid);
            MessageModel outputs = service.findAllOutputs(pageable, username, uuid.toString());
            logger.info("[USER : {}] || [UUID : {}] ---> OUTPUT MODULE ---> findAllOutputs() RESPONSE: {}", username, uuid, outputs.getMessage());
            outputs.setUuid(uuid.toString());

            model.addAttribute("result", outputs);
            model.addAttribute("listAreas", areaService.findAreas());
            model.addAttribute("listWarehouses", warehouseService.findWarehouses());
            model.addAttribute("listProducts", warehouseService.findWarehouseProductsByType(1, warehouseService.findWarehouses().get(0).getId()));
            model.addAttribute("products", "");
            if (outputs.getIsError()) {
                return ERROR_500;
            }
            model.addAttribute(PAGE_SIZE, pageable.getPageSize());
            return OUTPUT_REDIRECT;
        } catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> OUTPUT MODULE ---> findAllOutputs() ERROR: {}", username, uuid, exception.getMessage());
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

            logger.info("[USER : {}] || [UUID : {}] ---> EXECUTING OUTPUT MODULE ---> findById()", username, uuid);
            MessageModel output = service.findById(id, username, uuid.toString());
            output.setUuid(uuid.toString());
            logger.info("[USER : {}] || [UUID : {}] ---> OUTPUT MODULE ---> findById() RESPONSE: {}", username, uuid, output.getData() == null ? "null" : output.getData().toString());

            return output;
        }catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> OUTPUT MODULE ---> findById() ERROR: {}", username, uuid, exception.getMessage());
            MessageModel message = new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
            message.setUuid(uuid.toString());
            return message;
        }
    }

    @GetMapping("/product/{type}/{id}")
    @ResponseBody
    public List<ProductModel> findProductsByTypeAndWarehouse(HttpSession httpSession, @PathVariable("type") Integer type, @PathVariable("id") Long id) {
        UUID uuid = UUID.randomUUID();
        String username = "";
        try {
            SecurityContextImpl securityContext = (SecurityContextImpl) httpSession.getAttribute(SPRING_SECURITY_CONTEXT);
            SecurityUser user = (SecurityUser) securityContext.getAuthentication().getPrincipal();
            username = user.getUsername();

            logger.info("[USER : {}] || [UUID : {}] ---> EXECUTING OUTPUT MODULE ---> findProductsByTypeAndWarehouse()", username, uuid);
            List<ProductModel> products = warehouseService.findWarehouseProductsByType(type, id);
            logger.info("[USER : {}] || [UUID : {}] ---> OUTPUT MODULE ---> findProductsByTypeAndWarehouse() RESPONSE: {}", username, uuid, products == null ? "null" : products.toString());

            return products;
        }catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> OUTPUT MODULE ---> findProductsByTypeAndWarehouse() ERROR: {}", username, uuid, exception.getMessage());
            MessageModel message = new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
            message.setUuid(uuid.toString());
            return null;
        }
    }

    @GetMapping("/productWarehouse/{id}")
    @ResponseBody
    public WarehouseProductModel findWarehouseProduct(HttpSession httpSession, @PathVariable("id") Long id) {
        UUID uuid = UUID.randomUUID();
        String username = "";
        try {
            SecurityContextImpl securityContext = (SecurityContextImpl) httpSession.getAttribute(SPRING_SECURITY_CONTEXT);
            SecurityUser user = (SecurityUser) securityContext.getAuthentication().getPrincipal();
            username = user.getUsername();

            logger.info("[USER : {}] || [UUID : {}] ---> EXECUTING OUTPUT MODULE ---> findWarehouseProduct()", username, uuid);
            WarehouseProductModel warehouseProductModel = warehouseProductRepository.findByProduct_Id(id);
            logger.info("[USER : {}] || [UUID : {}] ---> OUTPUT MODULE ---> findWarehouseProduct() RESPONSE: {}", username, uuid, warehouseProductModel == null ? "null" : warehouseProductModel.toString());

            return warehouseProductModel;
        }catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> OUTPUT MODULE ---> findWarehouseProduct() ERROR: {}", username, uuid, exception.getMessage());
            MessageModel message = new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
            message.setUuid(uuid.toString());
            return null;
        }
    }

    @PostMapping("/save")
    public String saveOutput(@Valid @ModelAttribute("output") OutputModel output, BindingResult result, Model model, RedirectAttributes redirectAttributes, HttpSession httpSession, Pageable pageable, @RequestParam("products") String products) {
        UUID uuid = UUID.randomUUID();
        String username = "";
        try {
            SecurityContextImpl securityContext = (SecurityContextImpl) httpSession.getAttribute(SPRING_SECURITY_CONTEXT);
            SecurityUser user = (SecurityUser) securityContext.getAuthentication().getPrincipal();
            username = user.getUsername();
            logger.info("[USER : {}] || [UUID : {}] ---> EXECUTING OUTPUT MODULE ---> saveOutput()", username, uuid);
            model.addAttribute(ACTION, "save");
            model.addAttribute(PAGE_SIZE, pageable.getPageSize());
            List<ProductDTO> listProducts = new ArrayList<>();
            ObjectMapper mapper = new ObjectMapper();

            if(service.isExistOutput(output.getRequisition().getCode())){
                result.rejectValue("requisition.code", "output.requisition.code", "El código ya existe");
            }

            if(products.isEmpty() || products.equals("[]")){
                result.rejectValue("requisition.requisitionProductModels", "output.requisition.requisitionProductModels", "La salida debe tener al menos un producto");
            }else{
                listProducts = mapper.readValue(products, new TypeReference<List<ProductDTO>>(){});
                List<RequisitionProductModel> productsTmp = listProducts.stream().map(product -> {
                    RequisitionProductModel requisitionProductModel = new RequisitionProductModel();
                    requisitionProductModel.setProduct(productRepository.findById(product.getId()).get());
                    requisitionProductModel.setQuantity(product.getQuantity());
                    return requisitionProductModel;
                }).collect(Collectors.toList());
                for (RequisitionProductModel product : productsTmp) {
                    WarehouseProductModel warehouseProduct = warehouseProductRepository.findWarehouseProductByWarehouseAndProduct(output.getRequisition().getWarehouse(), product.getProduct());
                    if(warehouseProduct == null) {
                        result.rejectValue("requisition.requisitionProductModels", "output.requisition.requisitionProductModels", "Uno o más productos no existen en el almacén o no tienen la cantidad suficiente");
                        break;
                    }else if (warehouseProduct.getQuantity() < product.getQuantity()){
                        result.rejectValue("requisition.requisitionProductModels", "output.requisition.requisitionProductModels", "Uno o más productos no existen en el almacén o no tienen la cantidad suficiente");
                        break;
                    }
                }
                listProducts = mapper.readValue(products, new TypeReference<List<ProductDTO>>(){});
                for (ProductDTO product : listProducts) {
                    if(product.getQuantity() == null || product.getQuantity() <= 0){
                        result.rejectValue("requisition.requisitionProductModels", "output.requisition.requisitionProductModels", "Las cantidades de los productos deben ser mayor a 0");
                        break;
                    }
                }
            }

            if (!ArrayUtils.contains(new Integer[]{1, 2}, output.getRequisition().getType())){
                result.rejectValue("requisition.type", "output.requisition.type", "El tipo de pedido no es válido");
            }

            if(output.getArea() == null || output.getRequisition().getWarehouse() == null){
                if (output.getArea() == null) {
                    result.rejectValue("area", "output.area", "El area es requerido");
                }
                if (output.getRequisition().getWarehouse() == null) {
                    result.rejectValue("requisition.warehouse", "output.requisition.warehouse", "El almacén es requerido");
                }
            }else{
                boolean area = areaService.isExistById(output.getArea().getId());
                boolean ware = warehouseService.isExistById(output.getRequisition().getWarehouse().getId());

                if(!area){
                    result.rejectValue("area", "output.area", "El area es incorrecta");
                }
                if(!ware){
                    result.rejectValue("requisition.warehouse", "output.requisition.warehouse", "El almacén es incorrecto");
                }
            }

            if(result.hasErrors()) {
                logger.error("[USER : {}] || [UUID : {}] ---> OUTPUT MODULE ---> saveOutput() ERROR: {}", username, uuid, result.getAllErrors());
                MessageModel outputs = service.findAllOutputs(pageable, username, uuid.toString());
                outputs.setUuid(uuid.toString());
                outputs.setMessage(MessageCatalog.VALIDATION_ERROR);
                outputs.setIsError(true);
                model.addAttribute(RESULT, outputs);
                model.addAttribute("data", output);
                model.addAttribute("listAreas", areaService.findAreas());
                model.addAttribute("listWarehouses", warehouseService.findWarehouses());
                model.addAttribute("listProducts", productService.findProductsByType(1));
                model.addAttribute("products", products);
                model.addAttribute(PAGE_SIZE, pageable.getPageSize());
                return OUTPUT_REDIRECT;
            }

            Double total = 0.0;
            Integer quantity = 0;
            for (ProductDTO product : listProducts) {
                if(product.getQuantity() == null || product.getQuantity() <= 0){
                    result.rejectValue("requisition.requisitionProductModels", "output.requisition.requisitionProductModels", "Las cantidades de los productos deben ser mayor a 0");
                }
                total += product.getQuantity() * product.getUnitPrice();
                quantity += product.getQuantity();
            }
            output.getRequisition().setTotalAmount(total);
            output.getRequisition().setTotalOfProducts(quantity);

            MessageModel outputs = service.registerOutput(output, username, uuid.toString());
            OutputModel outputModel = (OutputModel) outputs.getData();
            RequisitionModel order = outputModel.getRequisition();

            for(ProductDTO product : listProducts) {
                ProductModel productModel = productRepository.findProductById(product.getId());
                requisitionProductRepository.save(new RequisitionProductModel(product.getQuantity(), order, productModel));
            }
            outputs.setUuid(uuid.toString());
            logger.info("[USER : {}] || [UUID : {}] ---> OUTPUT MODULE ---> saveOutput() RESPONSE: {}", username, uuid, outputs.getMessage());

            if (outputs.getIsError()){
                model.addAttribute(RESULT, outputs);
                return ERROR_500;
            }
            redirectAttributes.addFlashAttribute(RESULT_ACTION, outputs);
            logService.saveLog("SAVING NEW OUTPUT", 0L, username, uuid.toString());
            return OUTPUT_REDIRECT_LIST;
        }catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> OUTPUT MODULE ---> saveOutput() ERROR: {}", username, uuid, exception.getMessage());
            MessageModel message = new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
            message.setUuid(uuid.toString());
            model.addAttribute(RESULT, message);
            return ERROR_500;
        }
    }

    @PostMapping("/cancel")
    public String cancelOutput(Model model, @RequestParam long id, RedirectAttributes redirectAttributes, HttpSession httpSession) {
        UUID uuid = UUID.randomUUID();
        String username = "";
        try {
            SecurityContextImpl securityContext = (SecurityContextImpl) httpSession.getAttribute(SPRING_SECURITY_CONTEXT);
            SecurityUser user = (SecurityUser) securityContext.getAuthentication().getPrincipal();
            username = user.getUsername();
            logger.info("[USER : {}] || [UUID : {}] ---> EXECUTING OUTPUT MODULE ---> cancelOutput()", username, uuid);
            MessageModel outputs = service.cancelOutput(id, username, uuid.toString());
            logger.info("[USER : {}] || [UUID : {}] ---> OUTPUT MODULE ---> cancelOutput() RESPONSE: {}", username, uuid, outputs);
            outputs.setUuid(uuid.toString());

            if(outputs.getIsError()) {
                model.addAttribute(RESULT, outputs);
                return ERROR_500;
            }

            redirectAttributes.addFlashAttribute(RESULT_ACTION, outputs);
            logService.saveLog("CHANGING(CANCEL) STATUS OF OUTPUT", id, username, uuid.toString());
            return OUTPUT_REDIRECT_LIST;
        }catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> OUTPUT MODULE ---> cancelOutput() ERROR: {}", username, uuid, exception.getMessage());
            MessageModel message = new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
            message.setUuid(uuid.toString());
            model.addAttribute(RESULT, message);
            return ERROR_500;
        }
    }

    @PostMapping("/sent")
    public String sentOutput(Model model, @RequestParam long id, RedirectAttributes redirectAttributes, HttpSession httpSession) {
        UUID uuid = UUID.randomUUID();
        String username = "";
        try {
            SecurityContextImpl securityContext = (SecurityContextImpl) httpSession.getAttribute(SPRING_SECURITY_CONTEXT);
            SecurityUser user = (SecurityUser) securityContext.getAuthentication().getPrincipal();
            username = user.getUsername();
            logger.info("[USER : {}] || [UUID : {}] ---> EXECUTING OUTPUT MODULE ---> sentOutput()", username, uuid);
            MessageModel outputs = service.sentOutput(id, username, uuid.toString());
            logger.info("[USER : {}] || [UUID : {}] ---> OUTPUT MODULE ---> sentOutput() RESPONSE: {}", username, uuid, outputs);
            outputs.setUuid(uuid.toString());

            if(outputs.getMessage().name().equals("SUCCESS_SENT")) {
                OutputModel output = repository.findOutputById(id);
                List<RequisitionProductModel> products = output.getRequisition().getRequisitionProductModels();
                for (RequisitionProductModel product : products) {
                    WarehouseProductModel warehouseProduct = warehouseProductRepository.findWarehouseProductByWarehouseAndProduct(output.getRequisition().getWarehouse(), product.getProduct());
                    if(warehouseProduct == null) {
                        model.addAttribute(RESULT, outputs);
                        return ERROR_500;
                    }else {
                        warehouseProduct.setQuantity(warehouseProduct.getQuantity() - output.getRequisition().getRequisitionProductModels().stream().filter(requisitionProduct -> requisitionProduct.getProduct().getId() == product.getProduct().getId()).findFirst().get().getQuantity());
                        warehouseProductRepository.saveAndFlush(warehouseProduct);
                    }
                }
            }

            if(outputs.getIsError()) {
                model.addAttribute(RESULT, outputs);
                return ERROR_500;
            }

            redirectAttributes.addFlashAttribute(RESULT_ACTION, outputs);
            logService.saveLog("CHANGING(SENT) STATUS OF sentOutput", id, username, uuid.toString());
            return OUTPUT_REDIRECT_LIST;
        }catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> OUTPUT MODULE ---> sentOutput() ERROR: {}", username, uuid, exception.getMessage());
            MessageModel message = new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
            message.setUuid(uuid.toString());
            model.addAttribute(RESULT, message);
            return ERROR_500;
        }
    }
}
