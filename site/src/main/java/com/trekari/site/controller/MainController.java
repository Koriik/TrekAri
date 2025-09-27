package com.trekari.site.controller;

import com.trekari.site.model.Equipment;
import com.trekari.site.repository.EquipmentRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class MainController {

    private final EquipmentRepository repo;

    public MainController(EquipmentRepository repo) {
        this.repo = repo;
    }

    // Home page (landing page)
    @GetMapping("/")
    public String home() {
        return "index"; // serves index.html
    }

    // Equipment list page
    @GetMapping("/equipment") // fixed typo
    public String equipmentList(Model model) {
        var list = repo.findByAvailableTrue();
        System.out.println("Equipment found: " + list.size());  // <-- debug here
        model.addAttribute("equipmentList", list);
        return "equipment-list"; // template showing equipment list
    }

    // Equipment detail page
    @GetMapping("/equipment/{id}")
    public String equipmentDetail(@PathVariable Long id, Model model) {
        Equipment eq = repo.findById(id).orElseThrow();
        model.addAttribute("equipment", eq);
        return "equipment-detail"; // template showing single equipment
    }

    // Other static pages
    @GetMapping("/catalog")
    public String catalog() {
        return "catalog";
    }

    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

}
