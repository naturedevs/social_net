package com.orbvpn.api.domain.entity;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class Reseller {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @OneToOne(cascade = CascadeType.ALL)
  private User user;

  @Column
  private BigDecimal credit;

  @ManyToOne(fetch = FetchType.EAGER)
  private ResellerLevel level;

  @Column
  private LocalDateTime levelSetDate;

  @Column
  private String phone;

  @Column
  private boolean enabled = true;

  @ManyToMany
  private Set<ServiceGroup> serviceGroups = new HashSet<>();

  @Column
  @CreatedDate
  private LocalDateTime createdAt;

  @Column
  @LastModifiedDate
  private LocalDateTime updatedAt;

  @OneToMany(mappedBy = "reseller", cascade = CascadeType.REMOVE)
  private List<ResellerAddCredit> resellerAddCreditList;
}
