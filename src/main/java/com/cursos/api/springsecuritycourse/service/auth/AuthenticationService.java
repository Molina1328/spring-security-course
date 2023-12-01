package com.cursos.api.springsecuritycourse.service.auth;

import com.cursos.api.springsecuritycourse.dto.RegisteredUser;
import com.cursos.api.springsecuritycourse.dto.SaveUser;
import com.cursos.api.springsecuritycourse.dto.auth.AuthenticationRequest;
import com.cursos.api.springsecuritycourse.dto.auth.AuthenticationResponse;
import com.cursos.api.springsecuritycourse.persistence.entity.User;
import com.cursos.api.springsecuritycourse.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthenticationService {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

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

    // Generación de afirmaciones adicionales para el token JWT
    private Map<String, Object> generateExtraClaims(User user) {
        Map<String, Object> extraClaims = new HashMap<>(); //Se crea un objeto HashMap para almacenar las afirmaciones
        extraClaims.put("name",user.getName());
        extraClaims.put("role",user.getRole().name());
        extraClaims.put("authorities",user.getAuthorities());

        return extraClaims;
    }

    // Inicio de sesión y generación de token JWT
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

    // Validación de un token JWT
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
}
