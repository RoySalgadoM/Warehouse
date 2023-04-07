package mx.edu.utez.warehouse.requisition.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/requisition")
public class RequisitionController {
    @GetMapping("/list")
    public String findAllOrders(Model modelo) {
        return "order/order";
    }
}
