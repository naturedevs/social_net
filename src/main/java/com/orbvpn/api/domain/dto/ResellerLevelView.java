package com.orbvpn.api.domain.dto;

import com.orbvpn.api.domain.enums.ResellerLevelName;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResellerLevelView {
  private int id;
  private ResellerLevelName name;
  private BigDecimal discountPercent;
  private BigDecimal minScore;
}
