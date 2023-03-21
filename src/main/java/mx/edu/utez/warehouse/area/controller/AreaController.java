package mx.edu.utez.warehouse.area.controller;

import jakarta.servlet.http.HttpSession;
import mx.edu.utez.warehouse.WarehouseApplication;
import mx.edu.utez.warehouse.area.service.AreaServiceImpl;
import mx.edu.utez.warehouse.message.model.MessageModel;
import mx.edu.utez.warehouse.security.config.SecurityUser;
import mx.edu.utez.warehouse.utils.MessageCatalog;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Controller
@RequestMapping("/areas")
public class AreaController {
    private static final Logger logger = LogManager.getLogger(WarehouseApplication.class);
    @Autowired
    AreaServiceImpl service;

    @GetMapping("/")
    public String findAllAreas(Model model, HttpSession httpSession, @RequestParam int page, @RequestParam int size) {
        UUID uuid = UUID.randomUUID();
        String username = "";
        try {
            SecurityContextImpl securityContext = (SecurityContextImpl) httpSession.getAttribute("SPRING_SECURITY_CONTEXT");
            SecurityUser user = (SecurityUser) securityContext.getAuthentication().getPrincipal();
            username = user.getUsername();

            logger.info("[USER : " + username + "] || [UUID : " + uuid + "] ---> EXECUTING AREA MODULE ---> findAllAreas()");
            MessageModel areas = service.findAllAreas(page, size, username, uuid.toString());
            logger.info("[USER : " + username + "] || [UUID : " + uuid + "] ---> AREA MODULE --> findAllAreas() RESPONSE: " + areas.getData().toString());

            areas.setUuid(uuid.toString());
            model.addAttribute("result", areas);
            return "index";
        } catch (Exception exception) {
            logger.error("[USER : " + username + "] || [UUID : " + uuid + "] ---> AREA MODULE --> findAllAreas() ERROR: " + exception.getMessage());
            MessageModel message = new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
            message.setUuid(uuid.toString());
            model.addAttribute("result", message);
            return "index";
        }
    }

    @GetMapping("/{id}")
    public String findById(Model model, HttpSession httpSession, @PathVariable("id") Long id) {
        UUID uuid = UUID.randomUUID();
        String username = "";
        try {
            SecurityContextImpl securityContext = (SecurityContextImpl) httpSession.getAttribute("SPRING_SECURITY_CONTEXT");
            SecurityUser user = (SecurityUser) securityContext.getAuthentication().getPrincipal();
            username = user.getUsername();

            logger.info("[USER : " + username + "] || [UUID : " + uuid + "] ---> EXECUTING AREA MODULE ---> findById()");
            MessageModel area = service.findById(id, username, uuid.toString());
            logger.info("[USER : " + username + "] || [UUID : " + uuid + "] ---> AREA MODULE --> findById() RESPONSE: " + (area.getData() == null ? "null" : area.getData().toString()));

            area.setUuid(uuid.toString());
            model.addAttribute("result", area);
            return "index";
        }catch (Exception exception) {
            logger.error("[USER : " + username + "] || [UUID : " + uuid + "] ---> AREA MODULE --> findById() ERROR: " + exception.getMessage());
            MessageModel message = new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
            message.setUuid(uuid.toString());
            model.addAttribute("result", message);
            return "index";
        }
    }


}
