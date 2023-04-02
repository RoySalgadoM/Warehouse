package mx.edu.utez.warehouse.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/warehouse")
//@RequestMapping("/supplies")
public class IndexController {

    @GetMapping("/")
    public String listar(Model modelo) {
        return "warehouse/warehouse";
    }

    @GetMapping("/area")
    public String area(Model modelo) {
        return "area/area";
    }

    @GetMapping("/user")
    public String user(Model modelo) {
        return "user/user";
    }

    @GetMapping("/login")
    public String login(Model modelo) {
        return "login";
    }

    @GetMapping("/supplies")
    public String listar3(Model modelo) {
        return "supplier/supplies";
    }

    @GetMapping("/outputs")
    public String listar4(Model modelo) {
        return "output/outputs";
    }

    @GetMapping("/order")
    public String order(Model modelo) {
        return "order/order";
    }

    @GetMapping("/entry")
    public String emtry(Model modelo) {
        return "entry/entry";
    }


}
