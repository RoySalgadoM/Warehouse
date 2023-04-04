package mx.edu.utez.warehouse.warehouse.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/warehouse")
public class WarehouseController {

    @GetMapping("/")
    public String listar(Model modelo) {
        return "warehouse/warehouse";
    }
}
