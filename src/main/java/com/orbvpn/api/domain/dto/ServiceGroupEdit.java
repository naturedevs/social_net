package com.orbvpn.api.domain.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceGroupEdit {

  @NotBlank
  private String name;
  @NotBlank
  private String description;
  @NotNull
  private Locale language;
  @DecimalMin(value = "0.0")
  @DecimalMax(value = "100", inclusive = false)
  private BigDecimal discount = BigDecimal.ZERO;
  @NotEmpty
  private List<Integer> gateways;
  private List<Integer> allowedGeolocations = new ArrayList<>();
  private List<Integer> disAllowedGeolocations = new ArrayList<>();
}
