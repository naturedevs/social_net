package com.orbvpn.api.domain.dto;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountingView {
  private int totalUsers;
  private int joinedByDay;
  private int joinedByMonth;
  private int joinedByYear;

  private int monthPurchaseCount;
  private BigDecimal monthPurchase;

  private int dayPurchaseCount;
  private BigDecimal dayPurchase;

  private int monthRenewPurchaseCount;
  private BigDecimal monthRenewPurchase;

  private int dayRenewPurchaseCount;
  private BigDecimal dayRenewPurchase;
}
