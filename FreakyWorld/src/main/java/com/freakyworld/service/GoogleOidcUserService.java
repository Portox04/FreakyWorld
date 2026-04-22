package com.freakyworld.service;

import com.freakyworld.domain.Usuario;
import com.freakyworld.domain.UsuarioRol;
import java.util.ArrayList;
import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Service
public class GoogleOidcUserService extends OidcUserService {

    private final UsuarioService usuarioService;

    public GoogleOidcUserService(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);

        String correo = oidcUser.getEmail();
        String nombre = oidcUser.getFullName();

        if (correo == null || correo.isBlank()) {
            throw new OAuth2AuthenticationException("No se pudo obtener el correo del usuario desde Google");
        }

        Usuario usuario = usuarioService.registrarClienteGoogle(nombre, correo);
        List<UsuarioRol> rolesUsuario = usuarioService.listarRolesDeUsuario(usuario.getIdUsuario());

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (UsuarioRol usuarioRol : rolesUsuario) {
            authorities.add(new SimpleGrantedAuthority(usuarioRol.getRol().getNombre()));
        }

        return new DefaultOidcUser(authorities, oidcUser.getIdToken(), oidcUser.getUserInfo(), "email");
    }
}