package com.freakyworld.controller;

import com.freakyworld.domain.Factura;
import com.freakyworld.repository.FacturaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/pedidos")
public class FacturaController {

    private final FacturaRepository facturaRepository;

    public FacturaController(FacturaRepository facturaRepository) {
        this.facturaRepository = facturaRepository;
    }

    @GetMapping("/listado")
    public String listado(Model model) {
        model.addAttribute("pedidos", facturaRepository.findAllByOrderByFechaPedidoDesc());
        return "admin/pedidos";
    }

    @PostMapping("/actualizar/{idPedido}")
    public String actualizarEstado(@PathVariable Long idPedido,
                                   @RequestParam("estado") String estado,
                                   RedirectAttributes redirectAttributes) {

        Factura pedido = facturaRepository.findById(idPedido).orElse(null);

        if (pedido == null) {
            redirectAttributes.addFlashAttribute("error", "Pedido no encontrado");
            return "redirect:/admin/pedidos/listado";
        }

        pedido.setEstado(estado);
        facturaRepository.save(pedido);

        redirectAttributes.addFlashAttribute("mensaje", "Estado actualizado correctamente");
        return "redirect:/admin/pedidos/listado";
    }
}