package com.freakyworld;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/",
                    "/index",
                    "/login",
                    "/registro/**",
                    "/css/**",
                    "/js/**",
                    "/img/**",
                    "/producto/listado",
                    "/producto/ficha/**",
                    "/consultas/filtros",
                    "/consultas/listado"
                ).permitAll()

                // Categorías: ADMIN y VENDEDOR pueden ver
                .requestMatchers("/admin/**").hasAuthority("ADMIN")
                // Categorías: cualquiera puede ver el listado
                .requestMatchers("/categoria/listado").permitAll()

// Categorías: solo ADMIN puede modificar
                .requestMatchers("/categoria/crear").hasAuthority("ADMIN")
                .requestMatchers("/categoria/guardar").hasAuthority("ADMIN")
                .requestMatchers("/categoria/modifica/**").hasAuthority("ADMIN")
                .requestMatchers("/categoria/eliminar/**").hasAuthority("ADMIN")

                // Productos: solo ADMIN puede crear, guardar, editar y eliminar
                .requestMatchers("/producto/modifica/**").hasAuthority("ADMIN")
                .requestMatchers("/producto/guardar").hasAuthority("ADMIN")
                .requestMatchers("/producto/eliminar/**").hasAuthority("ADMIN")

                // Cliente: carrito, deseos, pedidos e interacciones de compra
                .requestMatchers("/carrito/**").hasAuthority("CLIENTE")
                .requestMatchers("/devolucion/**").hasAuthority("CLIENTE")
                .requestMatchers("/usuario/deseos/**").hasAuthority("CLIENTE")
                .requestMatchers("/usuario/pedidos/**").hasAuthority("CLIENTE")
                .requestMatchers("/interaccion/deseos/**").hasAuthority("CLIENTE")
                .requestMatchers("/interaccion/resena/**").hasAuthority("CLIENTE")

                // Perfil: cualquier autenticado
                .requestMatchers("/usuario/perfil/**").authenticated()

                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            .exceptionHandling(exception -> exception
                .accessDeniedPage("/error/403"))
            .csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}