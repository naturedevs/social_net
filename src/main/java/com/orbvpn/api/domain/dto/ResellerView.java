package com.orbvpn.api.domain.dto;

import com.orbvpn.api.domain.enums.ResellerLevelName;
import java.math.BigDecimal;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResellerView {
  private int id;
  private String email;
  private String firstName;
  private String lastName;
  private BigDecimal credit;
  private ResellerLevelName level;
  private String phone;
  private Set<ServiceGroupView> serviceGroups;
}
