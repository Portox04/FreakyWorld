package com.freakyworld.controller;

import com.freakyworld.domain.Usuario;
import com.freakyworld.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/registro")
public class RegistroController {package com.freakyworld.controller;

import com.freakyworld.domain.Factura;
import com.freakyworld.domain.ListaDeseos;
import com.freakyworld.domain.Usuario;
import com.freakyworld.repository.FacturaRepository;
import com.freakyworld.repository.ListaDeseosRepository;
import com.freakyworld.service.UsuarioService;
import java.security.Principal;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final FacturaRepository facturaRepository;
    private final ListaDeseosRepository listaDeseosRepository;

    public UsuarioController(UsuarioService usuarioService,
                             FacturaRepository facturaRepository,
                             ListaDeseosRepository listaDeseosRepository) {
        this.usuarioService = usuarioService;
        this.facturaRepository = facturaRepository;
        this.listaDeseosRepository = listaDeseosRepository;
    }

    @GetMapping("/perfil")
    public String perfil(Model model, Principal principal,
                         RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = obtenerUsuario(principal);
            model.addAttribute("usuario", usuario);
            return "usuario/perfil";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/login";
        }
    }

    @GetMapping("/pedidos")
    public String pedidos(Model model, Principal principal,
                          RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = obtenerUsuario(principal);
            List<Factura> pedidos = facturaRepository.findByUsuarioIdUsuario(usuario.getIdUsuario());
            model.addAttribute("pedidos", pedidos);
            return "usuario/pedidos";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/login";
        }
    }

    @GetMapping("/deseos")
    public String deseos(Model model, Principal principal,
                         RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = obtenerUsuario(principal);
            List<ListaDeseos> deseos = listaDeseosRepository.findByUsuarioIdUsuario(usuario.getIdUsuario());
            model.addAttribute("deseos", deseos);
            return "usuario/deseos";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/login";
        }
    }

    @GetMapping("/retorno")
    public String retorno() {
        return "usuario/retorno";
    }

    private Usuario obtenerUsuario(Principal principal) {
        if (principal == null) {
            throw new RuntimeException("Usuario no autenticado");
        }

        return usuarioService.buscarPorCorreo(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}package com.freakyworld.controller;

import com.freakyworld.domain.Factura;
import com.freakyworld.domain.ListaDeseos;
import com.freakyworld.domain.Usuario;
import com.freakyworld.repository.FacturaRepository;
import com.freakyworld.repository.ListaDeseosRepository;
import com.freakyworld.service.UsuarioService;
import java.security.Principal;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final FacturaRepository facturaRepository;
    private final ListaDeseosRepository listaDeseosRepository;

    public UsuarioController(UsuarioService usuarioService,
                             FacturaRepository facturaRepository,
                             ListaDeseosRepository listaDeseosRepository) {
        this.usuarioService = usuarioService;
        this.facturaRepository = facturaRepository;
        this.listaDeseosRepository = listaDeseosRepository;
    }

    @GetMapping("/perfil")
    public String perfil(Model model, Principal principal,
                         RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = obtenerUsuario(principal);
            model.addAttribute("usuario", usuario);
            return "usuario/perfil";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/login";
        }
    }

    @GetMapping("/pedidos")
    public String pedidos(Model model, Principal principal,
                          RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = obtenerUsuario(principal);
            List<Factura> pedidos = facturaRepository.findByUsuarioIdUsuario(usuario.getIdUsuario());
            model.addAttribute("pedidos", pedidos);
            return "usuario/pedidos";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/login";
        }
    }

    @GetMapping("/deseos")
    public String deseos(Model model, Principal principal,
                         RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = obtenerUsuario(principal);
            List<ListaDeseos> deseos = listaDeseosRepository.findByUsuarioIdUsuario(usuario.getIdUsuario());
            model.addAttribute("deseos", deseos);
            return "usuario/deseos";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/login";
        }
    }

    @GetMapping("/retorno")
    public String retorno() {
        return "usuario/retorno";
    }

    private Usuario obtenerUsuario(Principal principal) {
        if (principal == null) {
            throw new RuntimeException("Usuario no autenticado");
        }

        return usuarioService.buscarPorCorreo(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}

    private final UsuarioService usuarioService;

    public RegistroController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/nuevo")
    public String mostrarRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro/nuevo";
    }

    @PostMapping("/guardar")
    public String guardarUsuario(@ModelAttribute Usuario usuario,
                                 RedirectAttributes redirectAttributes) {
        try {
            usuarioService.registrarCliente(usuario);
            redirectAttributes.addFlashAttribute("mensaje", "Cuenta creada correctamente");
            return "redirect:/login";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/registro/nuevo";
        }
    }

    @GetMapping("/activa")
    public String activarCuenta() {
        return "registro/activa";
    }

    @GetMapping("/olvido")
    public String olvidoPassword() {
        return "registro/olvido";
    }
}