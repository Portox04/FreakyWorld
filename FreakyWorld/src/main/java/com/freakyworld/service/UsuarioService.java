package com.freakyworld.service;

import com.freakyworld.domain.Carrito;
import com.freakyworld.domain.Rol;
import com.freakyworld.domain.Usuario;
import com.freakyworld.domain.UsuarioRol;
import com.freakyworld.repository.CarritoRepository;
import com.freakyworld.repository.RolRepository;
import com.freakyworld.repository.UsuarioRepository;
import com.freakyworld.repository.UsuarioRolRepository;
import java.util.List;
import java.util.Optional;
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

    public UsuarioService(UsuarioRepository usuarioRepository,
                          RolRepository rolRepository,
                          UsuarioRolRepository usuarioRolRepository,
                          CarritoRepository carritoRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.usuarioRolRepository = usuarioRolRepository;
        this.carritoRepository = carritoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
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
    public void eliminar(Long idUsuario) {
        usuarioRepository.deleteById(idUsuario);
    }
}