package com.orbvpn.api.config.security;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.orbvpn.api.properties.JWTProperties;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
@Slf4j
public class JwtTokenUtil {

  private final JWTProperties jwtProperties;
  private final Algorithm algorithm;
  private final JWTVerifier jwtVerifier;

  public String generateAccessToken(UserDetails user) {

    return JWT.create()
      .withClaim("username", user.getUsername())
      .withIssuedAt(new Date())
      .withExpiresAt(new Date(System.currentTimeMillis() + jwtProperties.getExpirationMillis()))
      .sign(algorithm);
  }

  public String getUsername(String token) {
    DecodedJWT decodedJWT = jwtVerifier.verify(token);

    return decodedJWT.getClaim("username").asString();
  }

  public Date getExpirationDate(String token) {
    DecodedJWT decodedJWT = jwtVerifier.verify(token);
    return decodedJWT.getExpiresAt();
  }

  public boolean isTokenExpiring(String token) {
    Date expirationDate = getExpirationDate(token);
    return expirationDate.getTime() - System.currentTimeMillis() < jwtProperties
      .getRefreshMillis();
  }

  public boolean validate(String token) {
    try {
      jwtVerifier.verify(token);
      return true;
    } catch (Exception ex) {
      log.error("Invalid JWT signature - {}", ex.getMessage());
    }
    return false;
  }
}
