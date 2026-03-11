package com.freakyworld.controller;

import com.freakyworld.service.CategoriaService;
import com.freakyworld.service.ConsultaService;
import java.math.BigDecimal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/consultas")
public class ConsultaController {

    private final ConsultaService consultaService;
    private final CategoriaService categoriaService;

    public ConsultaController(ConsultaService consultaService,
                              CategoriaService categoriaService) {
        this.consultaService = consultaService;
        this.categoriaService = categoriaService;
    }

    @GetMapping("/filtros")
    public String mostrarFiltros(Model model) {
        model.addAttribute("categorias", categoriaService.listarCategorias());
        model.addAttribute("productos", consultaService.listarProductosActivos());
        return "consultas/filtros";
    }

    @GetMapping("/listado")
    public String listarFiltrados(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String serie,
            @RequestParam(required = false) Long idCategoria,
            @RequestParam(required = false) BigDecimal precioMin,
            @RequestParam(required = false) BigDecimal precioMax,
            Model model) {

        model.addAttribute("categorias", categoriaService.listarCategorias());
        model.addAttribute("productos",
                consultaService.filtrar(nombre, serie, idCategoria, precioMin, precioMax));

        model.addAttribute("nombre", nombre);
        model.addAttribute("serie", serie);
        model.addAttribute("idCategoria", idCategoria);
        model.addAttribute("precioMin", precioMin);
        model.addAttribute("precioMax", precioMax);

        return "consultas/listado";
    }
}