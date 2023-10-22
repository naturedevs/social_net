package com.orbvpn.api.domain.dto;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResellerLevelCoefficientsEdit {
  private BigDecimal monthCreditPercent;
  private BigDecimal monthCreditMax;
  private BigDecimal currentCreditPercent;
  private BigDecimal currentCreditMax;
  private BigDecimal activeSubscriptionPercent;
  private BigDecimal activeSubscriptionMax;
  private BigDecimal membershipDurationPercent;
  private BigDecimal membershipDurationMax;
  private BigDecimal depositIntervalManualDays;
  private BigDecimal depositIntervalMax;
  private BigDecimal totalSalePercent;
  private BigDecimal totalSaleMax;
  private BigDecimal monthSalePercent;
  private BigDecimal monthSaleMax;
}
