package com.orbvpn.api.domain.entity;

import com.orbvpn.api.domain.enums.ResellerLevelName;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class ResellerLevel {
  @Id
  private int id;

  @Column
  @Enumerated(EnumType.STRING)
  private ResellerLevelName name;

  @Column
  @DecimalMin(value = "0.0")
  @DecimalMax(value = "100.0")
  private BigDecimal discountPercent;

  @Column
  @DecimalMin(value = "0.0")
  @DecimalMax(value = "100.0")
  private BigDecimal minScore;
}
