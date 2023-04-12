package mx.edu.utez.warehouse.requisition.controller;

import jakarta.servlet.http.HttpSession;
import mx.edu.utez.warehouse.log.service.LogServiceImpl;
import mx.edu.utez.warehouse.message.model.MessageModel;
import mx.edu.utez.warehouse.requisition.service.RequisitionRepository;
import mx.edu.utez.warehouse.requisition.service.RequisitionServiceImpl;
import mx.edu.utez.warehouse.security.config.SecurityUser;
import mx.edu.utez.warehouse.utils.MessageCatalog;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.UUID;

@Controller
@RequestMapping("/order")
public class RequisitionController {
    private static final String SPRING_SECURITY_CONTEXT = "SPRING_SECURITY_CONTEXT";
    private static final Logger logger = LogManager.getLogger(RequisitionController.class);
    private static final String RESULT = "result";
    private static final String ERROR_500 = "errorPages/500";
    private static final String PAGE_SIZE = "pageSize";
    private static final String ORDER_REDIRECT = "requisition/order";
    @Autowired
    RequisitionServiceImpl service;
    @Autowired
    RequisitionRepository repository;
    @Autowired
    LogServiceImpl logService;

    @GetMapping("/list")
    public String findAllOrders(Model model, HttpSession httpSession, Pageable pageable) {
        UUID uuid = UUID.randomUUID();
        String username = "";
        try {
            SecurityContextImpl securityContext = (SecurityContextImpl) httpSession.getAttribute(SPRING_SECURITY_CONTEXT);
            SecurityUser user = (SecurityUser) securityContext.getAuthentication().getPrincipal();
            username = user.getUsername();
            logger.info("[USER : {}] || [UUID : {}] ---> EXECUTING REQUISITION MODULE ---> findAllOrders()", username, uuid);
            MessageModel entries = service.findAllRequisitions(pageable, username, uuid.toString());
            logger.info("[USER : {}] || [UUID : {}] ---> REQUISITION MODULE ---> findAllOrders() RESPONSE: {}", username, uuid, entries.getMessage());
            entries.setUuid(uuid.toString());

            model.addAttribute("result", entries);
            if (entries.getIsError()) {
                return ERROR_500;
            }
            model.addAttribute(PAGE_SIZE, pageable.getPageSize());
            return ORDER_REDIRECT;
        } catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> REQUISITION MODULE ---> findAllOrders() ERROR: {}", username, uuid, exception.getMessage());
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

            logger.info("[USER : {}] || [UUID : {}] ---> EXECUTING REQUISITION MODULE ---> findById()", username, uuid);
            MessageModel entry = service.findById(id, username, uuid.toString());
            entry.setUuid(uuid.toString());
            logger.info("[USER : {}] || [UUID : {}] ---> REQUISITION MODULE ---> findById() RESPONSE: {}", username, uuid, entry.getData() == null ? "null" : entry.getData().toString());

            return entry;
        }catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> REQUISITION MODULE ---> findById() ERROR: {}", username, uuid, exception.getMessage());
            MessageModel message = new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
            message.setUuid(uuid.toString());
            return message;
        }
    }
}
