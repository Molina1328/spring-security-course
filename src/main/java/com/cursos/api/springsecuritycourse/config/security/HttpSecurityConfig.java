package com.cursos.api.springsecuritycourse.config.security;

import com.cursos.api.springsecuritycourse.config.security.filter.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity // se habilita la funcionalidad de seguridad web proporcionado de Spring Security
public class HttpSecurityConfig {

    @Autowired
    private AuthenticationProvider daoAuthProvider;
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    // Configuración del filtro de seguridad HTTP
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // Deshabilitar la protección contra CSRF (Cross-Site Request Forgery) y establecer la política de creación de sesiones sin estado
        SecurityFilterChain filterChain = http
                .csrf(csrfConfig -> csrfConfig.disable()) // Se utiliza cuando se utiliza la seguridad basada en sesiones
                //Tipo de manejo de sesión || sessionCreationPolicy se agrega el estado de la sesion STATELESS es sin estado
                .sessionManagement(sessMagConfig -> sessMagConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Configuración del proveedor de autenticación personalizado
                .authenticationProvider(daoAuthProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                // Configuración de autorizaciones para las solicitudes HTTP
                .authorizeHttpRequests(authReqConfig -> {

                    // Permitir el acceso público a estas rutas específicas para crear usuarios y autenticar
                    authReqConfig.requestMatchers(HttpMethod.POST, "/customers").permitAll();
                    authReqConfig.requestMatchers(HttpMethod.POST, "/auth/authenticate").permitAll();
                    authReqConfig.requestMatchers(HttpMethod.GET, "/auth/validate-token").permitAll();

                    // Todas las demás solicitudes requieren autenticación
                    authReqConfig.anyRequest().authenticated();
                })
                .build();

        return filterChain;
    }
}
