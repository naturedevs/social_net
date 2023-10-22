package com.orbvpn.api.service;

import com.orbvpn.api.domain.dto.ReferralCodeView;
import com.orbvpn.api.domain.entity.ReferralCode;
import com.orbvpn.api.domain.entity.User;
import com.orbvpn.api.reposiitory.ReferralCodeRepository;
import com.orbvpn.api.reposiitory.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.Random;


@RequiredArgsConstructor
@Service
public class ReferralCodeService {
  private final UserRepository userRepository;
  private final ReferralCodeRepository referralCodeRepository;

  private final UserService userService;

  public ReferralCodeView getReferralCode(){
    User user = userService.getUser();
    ReferralCode referralCode = user.getReferralCode();
    if (referralCode == null) {
      referralCode = new ReferralCode();
      referralCode.setUser(user);
      referralCode.setCode(generateRandomString());
      referralCodeRepository.save(referralCode);
    }

    ReferralCodeView referralCodeView = new ReferralCodeView();
    referralCodeView.setCode(referralCode.getCode());
    return referralCodeView;
  }

  public String generateRandomString() {
    int leftLimit = 97; // letter 'a'
    int rightLimit = 122; // letter 'z'
    int targetStringLength = 10;
    Random random = new Random();
    StringBuilder buffer = new StringBuilder(targetStringLength);
    for (int i = 0; i < targetStringLength; i++) {
      int randomLimitedInt = leftLimit + (int)
              (random.nextFloat() * (rightLimit - leftLimit + 1));
      buffer.append((char) randomLimitedInt);
    }
    String generatedString = buffer.toString();
    return generatedString;
  }
}
