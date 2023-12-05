package com.cursos.api.springsecuritycourse.service.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    @Value("${security.jwt.expiration-in-minutes}")
    private Long EXPIRATION_IN_MINUTES;

    @Value("${security.jwt.secret-key}")
    private String SECRET_KEY;

    /**
     * Genera un token JWT con la información del usuario y reclamaciones adicionales.
     *
     * @param user        Detalles del usuario autenticado.
     * @param extraClaims Reclamaciones adicionales a incluir en el cuerpo del token.
     * @return Token JWT generado.
     */
    public String generateToken(UserDetails user, Map<String, Object> extraClaims) {

        Date issuedAt = new Date(System.currentTimeMillis());
        Date expiration = new Date((EXPIRATION_IN_MINUTES * 60 * 1000) + issuedAt.getTime());
        String jwt = Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(user.getUsername())
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .signWith(generateKey(), SignatureAlgorithm.HS256)
                .compact();

        return jwt;
    }

    /**
     * Genera una clave a partir de la clave secreta.
     *
     * @return Clave generada.
     */
    private Key generateKey() {
        byte[] passwordDecoded = Decoders.BASE64.decode(SECRET_KEY);
        System.out.println(new String(passwordDecoded));
        return Keys.hmacShaKeyFor(passwordDecoded);
    }

    /**
     * Extrae el nombre de usuario del token JWT.
     *
     * @param jwt Token JWT del cual extraer el nombre de usuario.
     * @return Nombre de usuario extraído.
     */
    public String extractUsername(String jwt) {
        return extractAllClaims(jwt).getSubject();
    }

    /**
     * Extrae todas las afirmaciones (claims) del token JWT.
     *
     * @param jwt Token JWT del cual extraer las afirmaciones.
     * @return Todas las afirmaciones del token.
     */
    private Claims extractAllClaims(String jwt) {
        return Jwts.parserBuilder().setSigningKey(generateKey()).build()
                .parseClaimsJws(jwt).getBody(); //parseClaimsJws para tokens firmados y parseClaimsJwt para tokens no firmados
    }

    /**
     * Extrae el token JWT de la cabecera de autorización de una solicitud HTTP.
     *
     * @param request Solicitud HTTP.
     * @return Token JWT extraído.
     */
    public String extractJwtFromRequest(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (!StringUtils.hasText(authorizationHeader) || !authorizationHeader.startsWith("Bearer ")) {
            return null;
        }
        return authorizationHeader.split(" ")[1];
    }

    /**
     * Extrae la fecha de expiración del token JWT.
     *
     * @param jwt Token JWT del cual extraer la fecha de expiración.
     * @return Fecha de expiración del token.
     */
    public Date extractExpiration(String jwt) {
        return extractAllClaims(jwt).getExpiration();
    }
}
