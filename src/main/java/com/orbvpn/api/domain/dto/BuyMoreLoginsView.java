package com.orbvpn.api.domain.dto;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class BuyMoreLoginsView {
  private double priceForMoreLogins;
  private String message;
}
