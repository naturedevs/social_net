package com.orbvpn.api.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResellerUserEdit {
  private String password;
  private Integer resellerId;
  private Integer groupId;
  private Integer multiLoginCount;
  private UserProfileEdit userProfileEdit;
}
