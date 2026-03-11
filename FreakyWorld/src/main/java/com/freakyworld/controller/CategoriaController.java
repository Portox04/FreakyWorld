package com.freakyworld.controller;

import com.freakyworld.domain.Categoria;
import com.freakyworld.service.CategoriaService;
import com.freakyworld.service.FirebaseStorageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/categoria")
public class CategoriaController {

    private final CategoriaService categoriaService;
    private final FirebaseStorageService firebaseStorageService;

    public CategoriaController(CategoriaService categoriaService,
                               FirebaseStorageService firebaseStorageService) {
        this.categoriaService = categoriaService;
        this.firebaseStorageService = firebaseStorageService;
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
                          @RequestParam(name = "imagen", required = false) MultipartFile imagen,
                          RedirectAttributes redirectAttributes) {
        try {
            if (imagen != null && !imagen.isEmpty()) {
                String urlImagen = firebaseStorageService.cargarImagen(imagen);
                categoria.setRutaImagen(urlImagen);
            }

            if (categoria.getIdCategoria() == null) {
                categoriaService.guardar(categoria);
                redirectAttributes.addFlashAttribute("mensaje", "Categoría creada correctamente");
            } else {
                Categoria categoriaActual = categoriaService.buscarPorId(categoria.getIdCategoria()).orElse(null);

                if (categoriaActual == null) {
                    redirectAttributes.addFlashAttribute("error", "Categoría no encontrada");
                    return "redirect:/categoria/listado";
                }

                if (imagen == null || imagen.isEmpty()) {
                    categoria.setRutaImagen(categoriaActual.getRutaImagen());
                }

                categoriaService.actualizar(categoria);
                redirectAttributes.addFlashAttribute("mensaje", "Categoría actualizada correctamente");
            }

            return "redirect:/categoria/listado";

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al guardar categoría: " + e.getMessage());
            return "redirect:/categoria/listado";
        }
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