package com.freakyworld.service;

import com.freakyworld.domain.Usuario;
import com.freakyworld.domain.UsuarioRol;
import com.freakyworld.repository.UsuarioRepository;
import com.freakyworld.repository.UsuarioRolRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioRolRepository usuarioRolRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository,
                                 UsuarioRolRepository usuarioRolRepository) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioRolRepository = usuarioRolRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        List<UsuarioRol> roles = usuarioRolRepository.findByUsuarioIdUsuario(usuario.getIdUsuario());

        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(usuarioRol -> new SimpleGrantedAuthority(usuarioRol.getRol().getNombre()))
                .collect(Collectors.toList());

        return org.springframework.security.core.userdetails.User.builder()
                .username(usuario.getCorreo())
                .password(usuario.getPassword())
                .authorities(authorities)
                .disabled(!Boolean.TRUE.equals(usuario.getActivo()))
                .build();
    }
}