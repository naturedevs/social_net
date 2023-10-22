package com.orbvpn.api.domain.dto;

import com.orbvpn.api.domain.enums.ServerType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServerView {
  private int id;
  private String hostName;
  private String publicIp;
  private String privateIp;
  private String city;
  private String country;
  private String secret;
  private Integer ports;
  private String sshUsername;
  private String sshKey;
  private String killCommand;
  private String rootCommand;
  private String description;
  private String continent;
  private ServerType type;
  private String hero;
  private String spot;
  private String zeus;
  private String bridgeIp;
  private String bridgeCountry;
}
