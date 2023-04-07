package mx.edu.utez.warehouse.entry.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/entry")
public class EntryController {
    @GetMapping("/list")
    public String findAllEntries(Model modelo) {
        return "entry/entry";
    }
}
