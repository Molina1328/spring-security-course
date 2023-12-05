package com.cursos.api.springsecuritycourse.persistence.repository.security;

import com.cursos.api.springsecuritycourse.persistence.entity.security.JwtToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface JwtTokenRepository extends JpaRepository<JwtToken, Long> {
    Optional<JwtToken> findByToken(String jwt);
}
