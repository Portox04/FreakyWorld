package com.freakyworld.controller;

import com.freakyworld.domain.Producto;
import com.freakyworld.repository.ResenaRepository;
import com.freakyworld.service.CategoriaService;
import com.freakyworld.service.FirebaseStorageService;
import com.freakyworld.service.ProductoService;
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
@RequestMapping("/producto")
public class ProductoController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;
    private final FirebaseStorageService firebaseStorageService;
    private final ResenaRepository resenaRepository;

    public ProductoController(ProductoService productoService,
                              CategoriaService categoriaService,
                              FirebaseStorageService firebaseStorageService,
                              ResenaRepository resenaRepository) {
        this.productoService = productoService;
        this.categoriaService = categoriaService;
        this.firebaseStorageService = firebaseStorageService;
        this.resenaRepository = resenaRepository;
    }

    @GetMapping("/listado")
    public String listado(Model model) {
        model.addAttribute("productos", productoService.listarProductos());
        return "producto/listado";
    }

 @GetMapping("/ficha/{idProducto}")
public String ficha(@PathVariable Long idProducto,
                    @RequestParam(name = "origen", required = false, defaultValue = "productos") String origen,
                    Model model,
                    RedirectAttributes redirectAttributes) {
    Producto producto = productoService.buscarPorId(idProducto).orElse(null);

    if (producto == null) {
        redirectAttributes.addFlashAttribute("error", "Producto no encontrado");
        return "redirect:/producto/listado";
    }

    model.addAttribute("producto", producto);
    model.addAttribute("resenas", resenaRepository.findByProductoIdProducto(idProducto));
    model.addAttribute("origen", origen);

    return "producto/ficha";
}

    @GetMapping("/modifica")
    public String nuevoProducto(Model model) {
        Producto producto = new Producto();
        producto.setActivo(true);

        model.addAttribute("producto", producto);
        model.addAttribute("categorias", categoriaService.listarCategorias());
        return "producto/modifica";
    }

    @GetMapping("/modifica/{idProducto}")
    public String modificarProducto(@PathVariable Long idProducto, Model model,
                                    RedirectAttributes redirectAttributes) {
        Producto producto = productoService.buscarPorId(idProducto).orElse(null);

        if (producto == null) {
            redirectAttributes.addFlashAttribute("error", "Producto no encontrado");
            return "redirect:/producto/listado";
        }

        model.addAttribute("producto", producto);
        model.addAttribute("categorias", categoriaService.listarCategorias());
        return "producto/modifica";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Producto producto,
                          @RequestParam(name = "imagenArchivo", required = false) MultipartFile imagenArchivo,
                          RedirectAttributes redirectAttributes) {
        try {
            if (imagenArchivo != null && !imagenArchivo.isEmpty()) {
                String urlImagen = firebaseStorageService.cargarImagen(imagenArchivo);
                producto.setRutaImagen(urlImagen);
            }

            if (producto.getIdProducto() == null) {
                productoService.guardar(producto);
                redirectAttributes.addFlashAttribute("mensaje", "Producto creado correctamente");
            } else {
                Producto productoActual = productoService.buscarPorId(producto.getIdProducto()).orElse(null);

                if (productoActual == null) {
                    redirectAttributes.addFlashAttribute("error", "Producto no encontrado");
                    return "redirect:/producto/listado";
                }

                if (imagenArchivo == null || imagenArchivo.isEmpty()) {
                    producto.setRutaImagen(productoActual.getRutaImagen());
                }

                productoService.actualizar(producto);
                redirectAttributes.addFlashAttribute("mensaje", "Producto actualizado correctamente");
            }

            return "redirect:/producto/listado";

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al guardar producto: " + e.getMessage());
            return "redirect:/producto/listado";
        }
    }

    @GetMapping("/eliminar/{idProducto}")
    public String eliminar(@PathVariable Long idProducto,
                           RedirectAttributes redirectAttributes) {
        try {
            productoService.eliminar(idProducto);
            redirectAttributes.addFlashAttribute("mensaje", "Producto eliminado correctamente");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/producto/listado";
    }
}