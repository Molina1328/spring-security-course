package com.cursos.api.springsecuritycourse.persistence.entity;

import com.cursos.api.springsecuritycourse.persistence.util.Role;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

@Entity
@Table(name = "\"user\"") // se pone así para que se confunda con la palabra reservada user ahora se llama "user" con comillas
public class User implements UserDetails { // Se implementa de UserDetails para poder utilizar los métodos para la authenticación
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private String username;
    private String name;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { // Devulve los roles que están permitidos para un usuario determinado
        if(role == null) return null;

        if(role.getPermissions() == null) return null;

        return role.getPermissions().stream()
                .map(each -> each.name())//Se mapea cada elemento de la lista de permisos al nombre del permiso
                .map(each -> new SimpleGrantedAuthority(each)) //Se mapea cada nombre de permiso a un objeto SimpleGrantedAuthority. Esta clase es parte de Spring Security y representa una autoridad o rol.
                //LO MISMO PERO CON UN SOLO MAP
//                .map(each -> {
//                    String permission = each.name();
//                    return new SimpleGrantedAuthority(permission);
//                })
                .collect(Collectors.toList());

    }


    @Override
    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {// Cuenta no expirada
        return true;
    }

    @Override
    public boolean isAccountNonLocked() { // Cuenta no bloqueada
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() { // Credenciales no experidas
        return true;
    }

    @Override
    public boolean isEnabled() { // Usuario Hábil
        return true;
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
