package com.orbvpn.api.domain.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceGroupView {

  private int id;
  private String name;
  private String description;
  private Locale language;
  private BigDecimal discount;
  private List<GatewayView> gateways;
  private List<GeolocationView> allowedGeolocations;
  private List<GeolocationView> disAllowedGeolocations;
  private List<GroupView> groups;
}
