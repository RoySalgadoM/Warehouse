package mx.edu.utez.warehouse.supplier.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/supplier")
public class SupplierController {
    @GetMapping("/list")
    public String findAllSupplies(Model modelo) {
        return "supplier/supplies";
    }
}
