package com.cursos.api.springsecuritycourse.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

@Configuration
public class SecurityBeansInjector {

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    @Bean //Strategias  de authenticación
    public AuthenticationProvider authenticationProvider (){
        DaoAuthenticationProvider authenticationStrategy = new DaoAuthenticationProvider(); //recibe el UserDatailService y la contraseña sin encriptar y mediante PassWord Encoder se la encripta
        authenticationStrategy.setPasswordEncoder(null);
        authenticationStrategy.setUserDetailsService(null);
        return authenticationStrategy;
    }
}
