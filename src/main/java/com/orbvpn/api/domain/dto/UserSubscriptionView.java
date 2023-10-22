package com.orbvpn.api.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSubscriptionView {
  private GroupView group;
  private int duration;
  private int multiLoginCount;
  private String downloadUpload;
  private String dailyBandwidth;
  private String expiresAt;
  private boolean expired;
  private String createdAt;
  private String updatedAt;
}
