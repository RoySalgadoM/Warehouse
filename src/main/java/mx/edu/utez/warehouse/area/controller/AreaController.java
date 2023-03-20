package mx.edu.utez.warehouse.area.controller;

import jakarta.servlet.http.HttpSession;
import mx.edu.utez.warehouse.WarehouseApplication;
import mx.edu.utez.warehouse.area.service.AreaServiceImpl;
import mx.edu.utez.warehouse.message.model.MessageModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
    public String findAllAreas(Model model, @RequestParam int page, @RequestParam int size){
        UUID uuid = UUID.randomUUID();
        logger.info("[USER : Roy Salgado] || [UUID : " + uuid.toString() + "] ---> EXECUTING AREA MODULE");
        MessageModel areas = service.findAllAreas(page, size);
        areas.setUuid(uuid.toString());
        logger.info("[USER : Roy Salgado] || [UUID : " + uuid.toString() + "] ---> AREA MODULE RESPONSE: " + areas.toString());
        model.addAttribute("result", areas);
        return "index";
    }

    @GetMapping("/private")
    public String findAllAreas(Model model, HttpSession httpSession){
        System.out.println(httpSession.getAttributeNames());
        return "dashboard";
    }
}
