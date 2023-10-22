package com.orbvpn.api.domain.dto;

import com.orbvpn.api.domain.enums.IpType;
import java.math.BigDecimal;
import java.math.BigInteger;
import javax.validation.constraints.*;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GroupEdit {

  @Positive
  private int serviceGroupId;

  @NotBlank
  private String name;

  @NotBlank
  private String description;

  @NotBlank
  private String tagName;

  @PositiveOrZero
  private int duration;

  @DecimalMin(value = "0.0", inclusive = false)
  @NotNull
  private BigDecimal price;

  private String usernamePostfix;

  private String usernamePostfixId;

  @DecimalMin(value = "0")
  private BigInteger dailyBandwidth = BigInteger.ZERO;

  private int multiLoginCount;

  @DecimalMin(value = "0")
  private BigInteger downloadUpload = BigInteger.ZERO;

  private IpType ip = IpType.STATIC;
}
