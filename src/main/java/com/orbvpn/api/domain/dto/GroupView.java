package com.orbvpn.api.domain.dto;

import com.orbvpn.api.domain.enums.IpType;
import java.math.BigDecimal;
import java.math.BigInteger;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupView {
  private int id;
  private ServiceGroupView serviceGroup;
  private String name;
  private String description;
  private String tagName;
  private int duration;
  private BigDecimal price;
  private String usernamePostfix;
  private String usernamePostfixId;
  private BigInteger dailyBandwidth;
  private int multiLoginCount;
  private BigInteger downloadUpload;
  private IpType ip;
}
