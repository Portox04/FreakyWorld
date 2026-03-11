package com.freakyworld.controller;

import com.freakyworld.service.DevolucionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/devoluciones")
public class AdminDevolucionController {

    private final DevolucionService devolucionService;

    public AdminDevolucionController(DevolucionService devolucionService) {
        this.devolucionService = devolucionService;
    }

    @GetMapping
    public String listado(Model model) {
        model.addAttribute("devoluciones", devolucionService.listarTodas());
        return "admin/devoluciones";
    }

    @PostMapping("/estado")
    public String cambiarEstado(@RequestParam Long idDevolucion,
                                @RequestParam String estado,
                                RedirectAttributes redirectAttributes) {
        try {
            devolucionService.cambiarEstado(idDevolucion, estado);
            redirectAttributes.addFlashAttribute("exito", "Estado actualizado correctamente");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/devoluciones";
    }
}