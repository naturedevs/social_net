package com.orbvpn.api.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GeolocationView {
  private int id;
  private String name;
  private String code;
  private String threeCharCode;
}
