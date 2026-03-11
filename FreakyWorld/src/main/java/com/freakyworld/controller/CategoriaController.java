package com.freakyworld.controller;

import com.freakyworld.domain.Categoria;
import com.freakyworld.service.CategoriaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/categoria")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping("/listado")
    public String listado(Model model) {
        model.addAttribute("categorias", categoriaService.listarCategorias());
        return "categoria/listado";
    }

    @GetMapping("/crear")
    public String crear(Model model) {
        model.addAttribute("categoria", new Categoria());
        return "categoria/crear";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Categoria categoria,
                          RedirectAttributes redirectAttributes) {
        try {
            if (categoria.getIdCategoria() == null) {
                categoriaService.guardar(categoria);
                redirectAttributes.addFlashAttribute("mensaje", "Categoría creada correctamente");
            } else {
                categoriaService.actualizar(categoria);
                redirectAttributes.addFlashAttribute("mensaje", "Categoría actualizada correctamente");
            }
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/categoria/listado";
    }

    @GetMapping("/modifica/{idCategoria}")
    public String modificar(@PathVariable Long idCategoria,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        Categoria categoria = categoriaService.buscarPorId(idCategoria).orElse(null);

        if (categoria == null) {
            redirectAttributes.addFlashAttribute("error", "Categoría no encontrada");
            return "redirect:/categoria/listado";
        }

        model.addAttribute("categoria", categoria);
        return "categoria/modifica";
    }

    @GetMapping("/eliminar/{idCategoria}")
    public String eliminar(@PathVariable Long idCategoria,
                           RedirectAttributes redirectAttributes) {
        try {
            categoriaService.eliminar(idCategoria);
            redirectAttributes.addFlashAttribute("mensaje", "Categoría eliminada correctamente");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/categoria/listado";
    }
}