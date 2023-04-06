package mx.edu.utez.warehouse.area.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import mx.edu.utez.warehouse.WarehouseApplication;
import mx.edu.utez.warehouse.area.model.AreaModel;
import mx.edu.utez.warehouse.area.service.AreaServiceImpl;
import mx.edu.utez.warehouse.message.model.MessageModel;
import mx.edu.utez.warehouse.security.config.SecurityUser;
import mx.edu.utez.warehouse.utils.MessageCatalog;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/area")
public class AreaController {
    private static final Logger logger = LogManager.getLogger(WarehouseApplication.class);
    @Autowired
    AreaServiceImpl service;

    @GetMapping("/list")
    public String findAllAreas(Model model, HttpSession httpSession, Pageable pageable, @ModelAttribute("area") AreaModel area) {
        UUID uuid = UUID.randomUUID();
        String username = "";
        try {
            SecurityContextImpl securityContext = (SecurityContextImpl) httpSession.getAttribute("SPRING_SECURITY_CONTEXT");
            SecurityUser user = (SecurityUser) securityContext.getAuthentication().getPrincipal();
            username = user.getUsername();
            logger.info("[USER : " + username + "] || [UUID : " + uuid + "] ---> EXECUTING AREA MODULE ---> findAllAreas()");
            MessageModel areas = service.findAllAreas(pageable, username, uuid.toString());
            logger.info("[USER : " + username + "] || [UUID : " + uuid + "] ---> AREA MODULE --> findAllAreas() RESPONSE: " + areas.toString());

            areas.setUuid(uuid.toString());
            model.addAttribute("result", areas);
            model.addAttribute("pageSize", pageable.getPageSize());
            return "area/area";
        } catch (Exception exception) {
            logger.error("[USER : " + username + "] || [UUID : " + uuid + "] ---> AREA MODULE --> findAllAreas() ERROR: " + exception.getMessage());
            MessageModel message = new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
            message.setUuid(uuid.toString());
            model.addAttribute("result", message);
            return "area/area";
        }
    }

    @GetMapping("/{id}")
    @ResponseBody
    public MessageModel findById(HttpSession httpSession, @PathVariable("id") Long id) {
        UUID uuid = UUID.randomUUID();
        String username = "";
        try {
            SecurityContextImpl securityContext = (SecurityContextImpl) httpSession.getAttribute("SPRING_SECURITY_CONTEXT");
            SecurityUser user = (SecurityUser) securityContext.getAuthentication().getPrincipal();
            username = user.getUsername();

            logger.info("[USER : " + username + "] || [UUID : " + uuid + "] ---> EXECUTING AREA MODULE ---> findById()");
            MessageModel updateArea = service.findById(id, username, uuid.toString());
            logger.info("[USER : " + username + "] || [UUID : " + uuid + "] ---> AREA MODULE --> findById() RESPONSE: " + (updateArea.getData() == null ? "null" : updateArea.getData().toString()));

            updateArea.setUuid(uuid.toString());
            return updateArea;
        }catch (Exception exception) {
            logger.error("[USER : " + username + "] || [UUID : " + uuid + "] ---> AREA MODULE --> findById() ERROR: " + exception.getMessage());
            MessageModel message = new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
            message.setUuid(uuid.toString());
            return message;
        }
    }

    @PostMapping("/save")
    public String saveArea(@Valid @ModelAttribute("area") AreaModel area, BindingResult result, Model model, RedirectAttributes redirectAttributes, HttpSession httpSession, Pageable pageable) {
        UUID uuid = UUID.randomUUID();
        String username = "";
        try {
            SecurityContextImpl securityContext = (SecurityContextImpl) httpSession.getAttribute("SPRING_SECURITY_CONTEXT");
            SecurityUser user = (SecurityUser) securityContext.getAuthentication().getPrincipal();
            username = user.getUsername();
            logger.info("[USER : " + username + "] || [UUID : " + uuid + "] ---> EXECUTING AREA MODULE ---> saveArea()");
            if(result.hasErrors()) {
                logger.error("[USER : " + username + "] || [UUID : " + uuid + "] ---> AREA MODULE --> saveArea() ERROR: " + result.getAllErrors().toString());
                MessageModel areas = service.findAllAreas(pageable, username, uuid.toString());
                areas.setUuid(uuid.toString());
                areas.setMessage(MessageCatalog.VALIDATION_ERROR);
                areas.setIsError(true);
                model.addAttribute("result", areas);
                model.addAttribute("data", area);
                model.addAttribute("action", "save");
                model.addAttribute("pageSize", pageable.getPageSize());

                return "area/area";
            }
            MessageModel areas = service.registerArea(area, username, uuid.toString());
            logger.info("[USER : " + username + "] || [UUID : " + uuid + "] ---> AREA MODULE --> saveArea() RESPONSE: " + areas.toString());

            areas.setUuid(uuid.toString());
            model.addAttribute("result", areas);
            model.addAttribute("pageSize", pageable.getPageSize());

            return "redirect:/area/list";
        }catch (Exception exception) {
            logger.error("[USER : " + username + "] || [UUID : " + uuid + "] ---> AREA MODULE --> saveArea() ERROR: " + exception.getMessage());
            MessageModel message = new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
            message.setUuid(uuid.toString());
            redirectAttributes.addFlashAttribute("result", "message");
            return "redirect:/area/list";
        }
    }
    @PostMapping("/update")
    public String updateArea(@Valid @ModelAttribute("area") AreaModel area, BindingResult result, Model model, RedirectAttributes redirectAttributes, HttpSession httpSession, Pageable pageable) {
        UUID uuid = UUID.randomUUID();
        String username = "";
        try {
            SecurityContextImpl securityContext = (SecurityContextImpl) httpSession.getAttribute("SPRING_SECURITY_CONTEXT");
            SecurityUser user = (SecurityUser) securityContext.getAuthentication().getPrincipal();
            username = user.getUsername();
            logger.info("[USER : " + username + "] || [UUID : " + uuid + "] ---> EXECUTING AREA MODULE ---> updateArea()");
            if(result.hasErrors()) {
                logger.error("[USER : " + username + "] || [UUID : " + uuid + "] ---> AREA MODULE --> updateArea() ERROR: " + result.getAllErrors().toString());
                MessageModel areas = service.findAllAreas(pageable, username, uuid.toString());
                areas.setUuid(uuid.toString());
                areas.setMessage(MessageCatalog.VALIDATION_ERROR);
                areas.setIsError(true);
                model.addAttribute("result", areas);
                model.addAttribute("data", area);
                model.addAttribute("action", "update");
                model.addAttribute("pageSize", pageable.getPageSize());

                return "area/area";
            }
            MessageModel areas = service.updateArea(area, username, uuid.toString());

            logger.info("[USER : " + username + "] || [UUID : " + uuid + "] ---> AREA MODULE --> updateArea() RESPONSE: " + areas.toString());

            areas.setUuid(uuid.toString());
            model.addAttribute("result", areas);
            model.addAttribute("pageSize", pageable.getPageSize());

            return "redirect:/area/list";
        }catch (Exception exception) {
            logger.error("[USER : " + username + "] || [UUID : " + uuid + "] ---> AREA MODULE --> updateArea() ERROR: " + exception.getMessage());
            MessageModel message = new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
            message.setUuid(uuid.toString());
            redirectAttributes.addFlashAttribute("result", "message");
            return "redirect:/area/list";
        }
    }
    @PostMapping("/disable")
    public String disableArea(Model model, @RequestParam long id, RedirectAttributes redirectAttributes, HttpSession httpSession) {
        UUID uuid = UUID.randomUUID();
        String username = "";
        try {
            SecurityContextImpl securityContext = (SecurityContextImpl) httpSession.getAttribute("SPRING_SECURITY_CONTEXT");
            SecurityUser user = (SecurityUser) securityContext.getAuthentication().getPrincipal();
            username = user.getUsername();
            logger.info("[USER : " + username + "] || [UUID : " + uuid + "] ---> EXECUTING AREA MODULE ---> disableArea()");
            MessageModel areas = service.disableArea(id, username, uuid.toString());
            logger.info("[USER : " + username + "] || [UUID : " + uuid + "] ---> AREA MODULE --> disableArea() RESPONSE: " + areas.toString());

            areas.setUuid(uuid.toString());
            model.addAttribute("result", areas);
            redirectAttributes.addFlashAttribute("msg_success", "¡Registro exitoso!");
            return "redirect:/area/list";
        }catch (Exception exception) {
            logger.error("[USER : " + username + "] || [UUID : " + uuid + "] ---> AREA MODULE --> disableArea() ERROR: " + exception.getMessage());
            MessageModel message = new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
            message.setUuid(uuid.toString());
            redirectAttributes.addFlashAttribute("result", "message");
            return "redirect:/area/list";
        }
    }




}
