package mx.edu.utez.warehouse.supplier.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/supplier")
public class SupplierController {
    @GetMapping("/")
    public String listar(Model modelo) {
        return "supplier/supplies";
    }
}
