package mx.edu.utez.warehouse.output.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/output")
public class OutputController {
    @GetMapping("/list")
    public String findAllOutputs(Model modelo) {
        return "output/outputs";
    }

}
