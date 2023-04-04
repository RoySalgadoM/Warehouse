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

    @GetMapping("/login")
    public String login(Model modelo) {
        return "login";
    }

    @GetMapping("/outputs")
    public String output(Model modelo) {
        return "output/outputs";
    }

    @GetMapping("/product")
    public String product(Model modelo) { return "product/product";
    }

    @GetMapping("/order")
    public String order(Model modelo) {
        return "order/order";
    }

    @GetMapping("/entry")
    public String entry(Model modelo) {
        return "entry/entry";
    }


}
