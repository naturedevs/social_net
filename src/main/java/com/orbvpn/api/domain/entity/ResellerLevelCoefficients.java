package com.orbvpn.api.domain.entity;

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class ResellerLevelCoefficients {
  @Id
  private int id;

  @Column
  private BigDecimal monthCreditPercent;

  @Column
  private BigDecimal monthCreditMax;

  @Column
  private BigDecimal currentCreditPercent;

  @Column
  private BigDecimal currentCreditMax;

  @Column
  private BigDecimal activeSubscriptionPercent;

  @Column
  private BigDecimal activeSubscriptionMax;

  @Column
  private BigDecimal membershipDurationPercent;

  @Column
  private BigDecimal membershipDurationMax;

  @Column
  private BigDecimal depositIntervalManualDays;

  @Column
  private BigDecimal depositIntervalMax;

  @Column
  private BigDecimal totalSalePercent;

  @Column
  private BigDecimal totalSaleMax;

  @Column
  private BigDecimal monthSalePercent;

  @Column
  private BigDecimal monthSaleMax;

}
