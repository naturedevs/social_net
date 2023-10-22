package com.orbvpn.api.config.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.orbvpn.api.properties.JWTProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
  private final JWTProperties jwtProperties;

  @Bean
  public Algorithm jwtAlgorithm() {
    return Algorithm.HMAC256(jwtProperties.getSecret());
  }

  @Bean
  public JWTVerifier verifier(Algorithm algorithm) {
    return JWT
      .require(algorithm)
      .build();
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12);
  }
}
