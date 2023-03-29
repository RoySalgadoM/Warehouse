package mx.edu.utez.warehouse.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/warehouse")
public class IndexController {

    @GetMapping("/")
    public String listar(Model modelo) {
        return "warehouse/warehouse";
    }
}
