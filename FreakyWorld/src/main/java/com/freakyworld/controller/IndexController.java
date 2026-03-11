package com.freakyworld.controller;

import com.freakyworld.service.ProductoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    private final ProductoService productoService;

    public IndexController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping({"/", "/index"})
    public String inicio(Model model) {
        model.addAttribute("productos", productoService.listarActivos());
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}