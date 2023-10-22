package com.orbvpn.api.domain.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class ResellerAddCredit {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @ManyToOne
  private Reseller reseller;

  @Column
  @DecimalMin(value = "0.0", inclusive = false)
  private BigDecimal credit;

  @Column
  @CreatedDate
  private LocalDateTime createdAt;

  public ResellerAddCredit(Reseller reseller, BigDecimal credit) {
    this.reseller = reseller;
    this.credit = credit;
  }
}

