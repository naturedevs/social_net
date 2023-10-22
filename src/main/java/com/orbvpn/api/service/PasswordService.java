package com.orbvpn.api.service;

import com.orbvpn.api.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PasswordService {

  private final PasswordEncoder passwordEncoder;
  private final RadiusService radiusService;

  public void setPassword(User user, String password) {
    user.setPassword(passwordEncoder.encode(password));
    user.setRadAccess(DigestUtils.sha1Hex(password));
    radiusService.editUserPassword(user);
  }
}
