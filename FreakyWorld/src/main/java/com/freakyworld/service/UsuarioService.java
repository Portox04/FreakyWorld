package com.freakyworld.service;

import com.freakyworld.domain.Carrito;
import com.freakyworld.domain.Devolucion;
import com.freakyworld.domain.DetalleFactura;
import com.freakyworld.domain.Factura;
import com.freakyworld.domain.Item;
import com.freakyworld.domain.ListaDeseos;
import com.freakyworld.domain.Resena;
import com.freakyworld.domain.Rol;
import com.freakyworld.domain.Usuario;
import com.freakyworld.domain.UsuarioRol;
import com.freakyworld.repository.CarritoRepository;
import com.freakyworld.repository.DevolucionRepository;
import com.freakyworld.repository.DetalleFacturaRepository;
import com.freakyworld.repository.FacturaRepository;
import com.freakyworld.repository.ItemRepository;
import com.freakyworld.repository.ListaDeseosRepository;
import com.freakyworld.repository.ResenaRepository;
import com.freakyworld.repository.RolRepository;
import com.freakyworld.repository.UsuarioRepository;
import com.freakyworld.repository.UsuarioRolRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final UsuarioRolRepository usuarioRolRepository;
    private final CarritoRepository carritoRepository;
    private final PasswordEncoder passwordEncoder;
    private final FacturaRepository facturaRepository;
    private final DetalleFacturaRepository detalleFacturaRepository;
    private final DevolucionRepository devolucionRepository;
    private final ResenaRepository resenaRepository;
    private final ListaDeseosRepository listaDeseosRepository;
    private final ItemRepository itemRepository;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          RolRepository rolRepository,
                          UsuarioRolRepository usuarioRolRepository,
                          CarritoRepository carritoRepository,
                          PasswordEncoder passwordEncoder,
                          FacturaRepository facturaRepository,
                          DetalleFacturaRepository detalleFacturaRepository,
                          DevolucionRepository devolucionRepository,
                          ResenaRepository resenaRepository,
                          ListaDeseosRepository listaDeseosRepository,
                          ItemRepository itemRepository) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.usuarioRolRepository = usuarioRolRepository;
        this.carritoRepository = carritoRepository;
        this.passwordEncoder = passwordEncoder;
        this.facturaRepository = facturaRepository;
        this.detalleFacturaRepository = detalleFacturaRepository;
        this.devolucionRepository = devolucionRepository;
        this.resenaRepository = resenaRepository;
        this.listaDeseosRepository = listaDeseosRepository;
        this.itemRepository = itemRepository;
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Rol> listarRoles() {
        return rolRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<UsuarioRol> listarRolesDeUsuario(Long idUsuario) {
        return usuarioRolRepository.findByUsuarioIdUsuario(idUsuario);
    }

    @Transactional
    public void asignarRol(Long idUsuario, Long idRol) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Rol rol = rolRepository.findById(idRol)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        if (usuarioRolRepository.existsByUsuarioIdUsuarioAndRolIdRol(idUsuario, idRol)) {
            throw new RuntimeException("Ese rol ya está asignado al usuario");
        }

        UsuarioRol usuarioRol = new UsuarioRol();
        usuarioRol.setUsuario(usuario);
        usuarioRol.setRol(rol);

        usuarioRolRepository.save(usuarioRol);
    }

    @Transactional
    public void quitarRol(Long idUsuario, Long idRol) {
        UsuarioRol usuarioRol = usuarioRolRepository
                .findByUsuarioIdUsuarioAndRolIdRol(idUsuario, idRol)
                .orElseThrow(() -> new RuntimeException("El usuario no tiene ese rol asignado"));

        usuarioRolRepository.delete(usuarioRol);
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorId(Long idUsuario) {
        return usuarioRepository.findById(idUsuario);
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

    @Transactional
    public Usuario guardar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario registrarCliente(Usuario usuario) {
        if (usuarioRepository.existsByCorreo(usuario.getCorreo())) {
            throw new RuntimeException("El correo ya está registrado");
        }

        usuario.setActivo(true);
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        Rol rolCliente = rolRepository.findByNombre("CLIENTE")
                .orElseThrow(() -> new RuntimeException("No existe el rol CLIENTE"));

        UsuarioRol usuarioRol = new UsuarioRol();
        usuarioRol.setUsuario(usuarioGuardado);
        usuarioRol.setRol(rolCliente);
        usuarioRolRepository.save(usuarioRol);

        Carrito carrito = new Carrito();
        carrito.setUsuario(usuarioGuardado);
        carritoRepository.save(carrito);

        return usuarioGuardado;
    }

    @Transactional
    public Usuario actualizar(Usuario usuario) {
        Usuario usuarioActual = usuarioRepository.findById(usuario.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuarioActual.setNombre(usuario.getNombre());
        usuarioActual.setCorreo(usuario.getCorreo());
        usuarioActual.setDireccion(usuario.getDireccion());
        usuarioActual.setActivo(usuario.getActivo());

        if (usuario.getPassword() != null && !usuario.getPassword().isBlank()) {
            usuarioActual.setPassword(passwordEncoder.encode(usuario.getPassword()));
        }

        return usuarioRepository.save(usuarioActual);
    }

    @Transactional
    public Usuario actualizarPerfil(Long idUsuario, String nombre, String correo,
                                    String direccion, String password) {

        Usuario usuarioActual = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (nombre == null || nombre.isBlank()) {
            throw new RuntimeException("El nombre no puede ir vacío");
        }

        if (correo == null || correo.isBlank()) {
            throw new RuntimeException("El correo no puede ir vacío");
        }

        if (usuarioRepository.existsByCorreoAndIdUsuarioNot(correo, idUsuario)) {
            throw new RuntimeException("Ese correo ya está en uso");
        }

        usuarioActual.setNombre(nombre);
        usuarioActual.setCorreo(correo);
        usuarioActual.setDireccion(direccion);

        if (password != null && !password.isBlank()) {
            usuarioActual.setPassword(passwordEncoder.encode(password));
        }

        return usuarioRepository.save(usuarioActual);
    }

    @Transactional
    public void eliminar(Long idUsuario) {
        usuarioRepository.deleteById(idUsuario);
    }

    @Transactional
    public void eliminarCuenta(Long idUsuario) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        try {
            List<Devolucion> devoluciones = devolucionRepository.findByUsuarioIdUsuario(idUsuario);
            if (!devoluciones.isEmpty()) {
                devolucionRepository.deleteAll(devoluciones);
            }

            List<Factura> facturas = facturaRepository.findByUsuarioIdUsuario(idUsuario);
            for (Factura factura : facturas) {
                List<DetalleFactura> detalles = detalleFacturaRepository.findByFacturaIdFactura(factura.getIdFactura());
                if (!detalles.isEmpty()) {
                    detalleFacturaRepository.deleteAll(detalles);
                }
            }

            if (!facturas.isEmpty()) {
                facturaRepository.deleteAll(facturas);
            }

            List<Resena> resenas = resenaRepository.findByUsuarioIdUsuario(idUsuario);
            if (!resenas.isEmpty()) {
                resenaRepository.deleteAll(resenas);
            }

            List<ListaDeseos> deseos = listaDeseosRepository.findByUsuarioIdUsuario(idUsuario);
            if (!deseos.isEmpty()) {
                listaDeseosRepository.deleteAll(deseos);
            }

            carritoRepository.findByUsuarioIdUsuario(idUsuario).ifPresent(carrito -> {
                List<Item> items = itemRepository.findByCarritoIdCarrito(carrito.getIdCarrito());
                if (!items.isEmpty()) {
                    itemRepository.deleteAll(items);
                }
                carritoRepository.delete(carrito);
            });

            List<UsuarioRol> rolesUsuario = usuarioRolRepository.findByUsuarioIdUsuario(idUsuario);
            if (!rolesUsuario.isEmpty()) {
                usuarioRolRepository.deleteAll(rolesUsuario);
            }

            usuarioRepository.delete(usuario);

        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("No se pudo eliminar la cuenta porque tiene información relacionada");
        } catch (Exception e) {
            throw new RuntimeException("No se pudo eliminar la cuenta");
        }
    }
}