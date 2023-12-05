package com.cursos.api.springsecuritycourse.service.auth;

import com.cursos.api.springsecuritycourse.dto.RegisteredUser;
import com.cursos.api.springsecuritycourse.dto.SaveUser;
import com.cursos.api.springsecuritycourse.dto.auth.AuthenticationRequest;
import com.cursos.api.springsecuritycourse.dto.auth.AuthenticationResponse;
import com.cursos.api.springsecuritycourse.exceptions.ObjectNotFoundException;
import com.cursos.api.springsecuritycourse.persistence.entity.User;
import com.cursos.api.springsecuritycourse.persistence.entity.security.JwtToken;
import com.cursos.api.springsecuritycourse.persistence.repository.security.JwtTokenRepository;
import com.cursos.api.springsecuritycourse.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthenticationService {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private JwtTokenRepository jwtRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * Registra un nuevo usuario y genera un token JWT para el mismo.
     *
     * @param newUser Datos del nuevo usuario a registrar.
     * @return Información del usuario registrado con el token JWT generado.
     */
    // Registro de un nuevo usuario y generación de token JWT
    public RegisteredUser registerOneCustomer(SaveUser newUser) {
        // Registro del nuevo usuario en la base de datos
        User user = userService.registrOneCustomer(newUser);

        // Creación de un objeto RegisteredUser con información básica del usuario
        RegisteredUser userDto = new RegisteredUser();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setUsername(user.getUsername());
        userDto.setRole(user.getRole().name());

        // Generación de un token JWT para el usuario registrado
        String jwt = jwtService.generateToken(user, generateExtraClaims(user));
        userDto.setJwt(jwt);

        return userDto;
    }

    /**
     * Genera afirmaciones adicionales para el token JWT.
     *
     * @param user Usuario para el cual generar las afirmaciones adicionales.
     * @return Mapa de afirmaciones adicionales.
     */
    private Map<String, Object> generateExtraClaims(User user) {
        Map<String, Object> extraClaims = new HashMap<>(); // Se crea un objeto HashMap para almacenar las afirmaciones
        extraClaims.put("name", user.getName());
        extraClaims.put("role", user.getRole().name());
        extraClaims.put("authorities", user.getAuthorities());

        return extraClaims;
    }

    /**
     * Inicia sesión y genera un token JWT para el usuario autenticado.
     *
     * @param autRequest Datos de autenticación del usuario.
     * @return Respuesta de autenticación con el token JWT generado.
     */
    public AuthenticationResponse login(AuthenticationRequest autRequest) {
        // Creación de un objeto de autenticación con nombre de usuario y contraseña
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                autRequest.getUsername(), autRequest.getPassword()
        );

        // Autenticación del usuario utilizando el AuthenticationManager
        authenticationManager.authenticate(authentication);

        // Obtención de detalles del usuario autenticado
        UserDetails user = userService.findOneByUsername(autRequest.getUsername()).get();

        // Generación de un token JWT para el usuario autenticado
        String jwt = jwtService.generateToken(user, generateExtraClaims((User) user));

        // Creación de una respuesta de autenticación con el token JWT
        AuthenticationResponse authRsp = new AuthenticationResponse();
        authRsp.setJwt(jwt);

        return authRsp;
    }

    /**
     * Valida un token JWT.
     *
     * @param jwt Token JWT a validar.
     * @return true si el token es válido, false en caso contrario.
     */
    public boolean validateToken(String jwt) {
        try {
            // Intento de extraer el nombre de usuario del token
            jwtService.extractUsername(jwt);
            return true; // Si tiene éxito, el token es válido
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false; // Si hay un error, el token no es válido
        }
    }

    /**
     * Encuentra al usuario autenticado.
     *
     * @return Usuario autenticado.
     */
    public User findLoggedInUser() {
        UsernamePasswordAuthenticationToken auth =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        String username = (String) auth.getPrincipal();
        return userService.findOneByUsername(username)
                .orElseThrow(() -> new ObjectNotFoundException("User not found. Username: " + username));
    }

    /**
     * Cierra la sesión del usuario (invalida el token).
     *
     * @param request Solicitud HTTP que contiene el token JWT.
     */
    public void logout(HttpServletRequest request) {

        String jwt = jwtService.extractJwtFromRequest(request);
        if (jwt == null || !StringUtils.hasText(jwt)) return;

        Optional<JwtToken> token = jwtRepository.findByToken(jwt);

        if (token.isPresent() && token.get().isValid()) {
            token.get().setValid(false);
            jwtRepository.save(token.get());
        }
    }
}

