package com.freakyworld.controller;

import com.freakyworld.domain.Cupon;
import com.freakyworld.service.CuponService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/cupones")
public class CuponController {

    private final CuponService cuponService;

    public CuponController(CuponService cuponService) {
        this.cuponService = cuponService;
    }

    @GetMapping("/listado")
    public String listado(Model model) {
        model.addAttribute("cupones", cuponService.listarCupones());
        return "cupon/listado";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        Cupon cupon = new Cupon();
        cupon.setActivo(true);

        model.addAttribute("cupon", cupon);
        return "cupon/modifica";
    }

    @GetMapping("/modifica/{idCupon}")
    public String modifica(@PathVariable Long idCupon,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("cupon", cuponService.obtenerCuponPorId(idCupon));
            return "cupon/modifica";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/cupones/listado";
        }
    }

    @PostMapping("/guardar")
    public String guardar(Cupon cupon, RedirectAttributes redirectAttributes) {
        try {
            cuponService.guardar(cupon);

            if (cupon.getIdCupon() == null) {
                redirectAttributes.addFlashAttribute("mensaje", "Cupón creado correctamente");
            } else {
                redirectAttributes.addFlashAttribute("mensaje", "Cupón actualizado correctamente");
            }

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return cupon.getIdCupon() == null
                    ? "redirect:/admin/cupones/nuevo"
                    : "redirect:/admin/cupones/modifica/" + cupon.getIdCupon();
        }

        return "redirect:/admin/cupones/listado";
    }

    @GetMapping("/estado/{idCupon}")
    public String cambiarEstado(@PathVariable Long idCupon,
                                RedirectAttributes redirectAttributes) {
        try {
            cuponService.cambiarEstado(idCupon);
            redirectAttributes.addFlashAttribute("mensaje", "Estado del cupón actualizado correctamente");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/admin/cupones/listado";
    }
}