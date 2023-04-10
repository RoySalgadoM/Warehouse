package mx.edu.utez.warehouse.area.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import mx.edu.utez.warehouse.area.model.AreaModel;
import mx.edu.utez.warehouse.area.service.AreaServiceImpl;
import mx.edu.utez.warehouse.message.model.MessageModel;
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

import java.util.UUID;

@Controller
@RequestMapping("/area")
public class AreaController {
    private static final String SPRING_SECURITY_CONTEXT = "SPRING_SECURITY_CONTEXT";
    private static final Logger logger = LogManager.getLogger(AreaController.class);
    @Autowired
    AreaServiceImpl service;

    @GetMapping("/list")
    public String findAllAreas(Model model, HttpSession httpSession, Pageable pageable, @ModelAttribute("area") AreaModel area) {
        UUID uuid = UUID.randomUUID();
        String username = "";
        try {
            SecurityContextImpl securityContext = (SecurityContextImpl) httpSession.getAttribute(SPRING_SECURITY_CONTEXT);
            SecurityUser user = (SecurityUser) securityContext.getAuthentication().getPrincipal();
            username = user.getUsername();
            logger.info("[USER : {}] || [UUID : {}] ---> EXECUTING AREA MODULE ---> findAllAreas()", username, uuid);
            MessageModel areas = service.findAllAreas(pageable, username, uuid.toString());
            logger.info("[USER : {}] || [UUID : {}] ---> AREA MODULE ---> findAllAreas() RESPONSE: {}", username, uuid, areas.getMessage());
            areas.setUuid(uuid.toString());

            model.addAttribute("result", areas);
            if (areas.getIsError()) {
                return "errorPages/500";
            }

            model.addAttribute("pageSize", pageable.getPageSize());
            return "area/area";
        } catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> AREA MODULE ---> findAllAreas() ERROR: {}", username, uuid, exception.getMessage());
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

            logger.info("[USER : {}] || [UUID : {}] ---> EXECUTING AREA MODULE ---> findById()", username, uuid);
            MessageModel area = service.findById(id, username, uuid.toString());
            area.setUuid(uuid.toString());
            logger.info("[USER : {}] || [UUID : {}] ---> AREA MODULE ---> findById() RESPONSE: {}", username, uuid, area.getData() == null ? "null" : area.getData().toString());

            return area;
        }catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> AREA MODULE ---> findById() ERROR: {}", username, uuid, exception.getMessage());
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
            SecurityContextImpl securityContext = (SecurityContextImpl) httpSession.getAttribute(SPRING_SECURITY_CONTEXT);
            SecurityUser user = (SecurityUser) securityContext.getAuthentication().getPrincipal();
            username = user.getUsername();
            logger.info("[USER : {}] || [UUID : {}] ---> EXECUTING AREA MODULE ---> saveArea()", username, uuid);
            model.addAttribute("action", "save");
            model.addAttribute("pageSize", pageable.getPageSize());

            if(service.isExistArea(area.getIdentifier())){
                result.rejectValue("identifier", "area.identifier", "El identificador ya existe");
            }
            if(result.hasErrors()) {
                logger.error("[USER : {}] || [UUID : {}] ---> AREA MODULE ---> saveArea() ERROR: {}", username, uuid, result.getAllErrors());
                MessageModel areas = service.findAllAreas(pageable, username, uuid.toString());
                areas.setUuid(uuid.toString());
                areas.setMessage(MessageCatalog.VALIDATION_ERROR);
                areas.setIsError(true);
                model.addAttribute("result", areas);
                model.addAttribute("data", area);

                return "area/area";
            }
            MessageModel areas = service.registerArea(area, username, uuid.toString());
            areas.setUuid(uuid.toString());
            logger.info("[USER : {}] || [UUID : {}] ---> AREA MODULE ---> saveArea() RESPONSE: {}", username, uuid, areas.getMessage());

            if (areas.getIsError()){
                model.addAttribute("result", areas);
                return "errorPages/500";
            }
            redirectAttributes.addFlashAttribute("resultAction", areas);
            return "redirect:/area/list";
        }catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> AREA MODULE ---> saveArea() ERROR: {}", username, uuid, exception.getMessage());
            MessageModel message = new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
            message.setUuid(uuid.toString());
            model.addAttribute("result", message);
            return "errorPages/500";
        }
    }
    @PostMapping("/update")
    public String updateArea(@Valid @ModelAttribute("area") AreaModel area, BindingResult result, Model model, RedirectAttributes redirectAttributes, HttpSession httpSession, Pageable pageable) {
        UUID uuid = UUID.randomUUID();
        String username = "";
        try {
            SecurityContextImpl securityContext = (SecurityContextImpl) httpSession.getAttribute(SPRING_SECURITY_CONTEXT);
            SecurityUser user = (SecurityUser) securityContext.getAuthentication().getPrincipal();
            username = user.getUsername();
            logger.info("[USER : {}] || [UUID : {}] ---> EXECUTING AREA MODULE ---> updateArea()", username, uuid);

            if(service.isExistAreaAndId(area.getIdentifier(), area.getId())){
                result.rejectValue("identifier", "area.identifier", "El identificador ya existe");
            }
            if(result.hasErrors()) {
                logger.error("[USER : {}] || [UUID : {}] ---> AREA MODULE ---> updateArea() ERROR: {}", username, uuid, result.getAllErrors());
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
            areas.setUuid(uuid.toString());

            logger.info("[USER : {}] || [UUID : {}] ---> AREA MODULE ---> updateArea() RESPONSE: {}", username, uuid, areas.getMessage());
            if(areas.getIsError()) {
                model.addAttribute("result", areas);
                return "errorPages/500";
            }
            redirectAttributes.addFlashAttribute("action", "update");
            redirectAttributes.addFlashAttribute("pageSize", pageable.getPageSize());
            redirectAttributes.addFlashAttribute("resultAction", areas);
            return "redirect:/area/list";
        }catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> AREA MODULE ---> updateArea() ERROR: {}", username, uuid, exception.getMessage());
            MessageModel message = new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
            message.setUuid(uuid.toString());
            model.addAttribute("result", message);
            return "errorPages/500";
        }
    }
    @PostMapping("/disable")
    public String disableArea(Model model, @RequestParam long id, RedirectAttributes redirectAttributes, HttpSession httpSession) {
        UUID uuid = UUID.randomUUID();
        String username = "";
        try {
            SecurityContextImpl securityContext = (SecurityContextImpl) httpSession.getAttribute(SPRING_SECURITY_CONTEXT);
            SecurityUser user = (SecurityUser) securityContext.getAuthentication().getPrincipal();
            username = user.getUsername();
            logger.info("[USER : {}] || [UUID : {}] ---> EXECUTING AREA MODULE ---> disableArea()", username, uuid);
            MessageModel areas = service.disableArea(id, username, uuid.toString());
            logger.info("[USER : {}] || [UUID : {}] ---> AREA MODULE ---> disableArea() RESPONSE: {}", username, uuid, areas);
            areas.setUuid(uuid.toString());

            if(areas.getIsError()) {
                model.addAttribute("result", areas);
                return "errorPages/500";
            }

            redirectAttributes.addFlashAttribute("resultAction", areas);
            return "redirect:/area/list";
        }catch (Exception exception) {
            logger.error("[USER : {}] || [UUID : {}] ---> AREA MODULE ---> disableArea() ERROR: {}", username, uuid, exception.getMessage());
            MessageModel message = new MessageModel(MessageCatalog.UNK_ERROR_FOUND, null, true);
            message.setUuid(uuid.toString());
            model.addAttribute("result", message);
            return "errorPages/500";
        }
    }
}
