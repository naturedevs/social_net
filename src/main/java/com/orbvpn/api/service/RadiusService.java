package com.orbvpn.api.service;


import com.orbvpn.api.domain.entity.*;
import com.orbvpn.api.exception.NotFoundException;
import com.orbvpn.api.reposiitory.NasRepository;
import com.orbvpn.api.reposiitory.RadAcctRepository;
import com.orbvpn.api.reposiitory.RadCheckRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RadiusService {
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMMM dd yyyy HH:mm:ss");

  private final NasRepository nasRepository;
  private final RadCheckRepository radCheckRepository;
  private final RadAcctRepository radAcctRepository;


  public void createNas(Server server) {
    Nas nas = new Nas();
    mapNas(nas, server);
    nasRepository.save(nas);
  }

  public void editNas(String nasname, Server server) {
    Nas nas = getNasByName(nasname);
    mapNas(nas, server);
    nasRepository.save(nas);
  }

  public void deleteNas(Server server) {
    Nas nas = getNasByName(server.getPublicIp());
    nasRepository.delete(nas);
  }

  public void mapNas(Nas nas, Server server) {
    nas.setNasName(server.getPublicIp());
    nas.setShortName(server.getHostName());
    nas.setType(server.getType());
    nas.setSecret(server.getSecret());
  }

  public void updateUserExpirationRadCheck(UserSubscription userSubscription) {
    User user = userSubscription.getUser();
    String username = user.getUsername();
    String updatedExpireDate = convertToExpirationString(userSubscription.getExpiresAt());

    RadCheck radCheck = radCheckRepository.findByAttributeAndUsername("Expiration",username).get(0);
    radCheck.setValue(updatedExpireDate);

    radCheckRepository.save(radCheck);
  }

  @Transactional
  public void createUserRadChecks(UserSubscription userSubscription) {
    User user = userSubscription.getUser();
    String username = user.getUsername();

    //Password
    String sha1Hex = user.getRadAccess();
    RadCheck passwordCheck = new RadCheck();
    passwordCheck.setUsername(username);
    passwordCheck.setAttribute("SHA-Password");
    passwordCheck.setOp(":=");
    passwordCheck.setValue(sha1Hex);

    //Simultaneous use
    RadCheck simultaneousCheck = new RadCheck();
    simultaneousCheck.setUsername(username);
    simultaneousCheck.setAttribute("Simultaneous-Use");
    simultaneousCheck.setOp(":=");
    simultaneousCheck.setValue(String.valueOf(userSubscription.getMultiLoginCount()));

    //Expiration
    RadCheck expirationCheck = new RadCheck();
    expirationCheck.setUsername(username);
    expirationCheck.setAttribute("Expiration");
    expirationCheck.setOp("==");
    expirationCheck.setValue(convertToExpirationString(userSubscription.getExpiresAt()));

    radCheckRepository.save(passwordCheck);
    radCheckRepository.save(simultaneousCheck);
    radCheckRepository.save(expirationCheck);
  }

  public void deleteUserRadChecks(User user) {
    radCheckRepository.deleteByUsername(user.getUsername());
  }

  public void deleteUserRadAcct(User user) {
    radAcctRepository.deleteByUsername(user.getUsername());
  }

  public void editUserPassword(User user) {
    String username = user.getUsername();
    String sha1Hex = user.getRadAccess();

    Optional<RadCheck> radCheckOptional = radCheckRepository
      .findByUsernameAndAttribute(username, "SHA-Password");

    if(radCheckOptional.isPresent()) {
      RadCheck radCheck = radCheckOptional.get();
      radCheck.setValue(sha1Hex);
      radCheckRepository.save(radCheck);
    }
  }

  public void editUserMoreLoginCount(User user, int multiLoginCount) {
    String username = user.getUsername();

    Optional<RadCheck> radCheckOptional = radCheckRepository
      .findByUsernameAndAttribute(username, "Simultaneous-Use");

    if(radCheckOptional.isPresent()) {
      RadCheck radCheck = radCheckOptional.get();
      radCheck.setValue(String.valueOf(multiLoginCount));
      radCheckRepository.save(radCheck);
    }
  }

  public void addUserMoreLoginCount(User user, int moreLoginCount) {
    String username = user.getUsername();

    Optional<RadCheck> radCheckOptional = radCheckRepository
      .findByUsernameAndAttribute(username, "Simultaneous-Use");

    if(radCheckOptional.isPresent()) {
      RadCheck radCheck = radCheckOptional.get();
      Integer currentValue = Integer.valueOf(radCheck.getValue());
      radCheck.setValue(String.valueOf(moreLoginCount + currentValue));
      radCheckRepository.save(radCheck);
    }
  }

  public void subUserMoreLoginCount(User user, int moreLoginCount) {
    String username = user.getUsername();

    Optional<RadCheck> radCheckOptional = radCheckRepository
      .findByUsernameAndAttribute(username, "Simultaneous-Use");

    if(radCheckOptional.isPresent()) {
      RadCheck radCheck = radCheckOptional.get();
      Integer currentValue = Integer.valueOf(radCheck.getValue());
      Integer newValue = currentValue - moreLoginCount;
      newValue = newValue <= 0 ? 1 : newValue;

      radCheck.setValue(String.valueOf(newValue));
      radCheckRepository.save(radCheck);
    }
  }

  public String convertToExpirationString(LocalDateTime expiration) {
    ZonedDateTime ldtZoned = expiration.atZone(ZoneId.systemDefault());
    ZonedDateTime utcZoned = ldtZoned.withZoneSameInstant(ZoneId.of("UTC"));
    return DATE_FORMATTER.format(utcZoned);
  }

  public Nas getNasByName(String nasName) {
    return nasRepository.findByNasName(nasName)
      .orElseThrow(()->new NotFoundException(Nas.class, nasName));
  }
}
