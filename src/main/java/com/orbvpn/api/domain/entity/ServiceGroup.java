package com.orbvpn.api.domain.entity;

import com.orbvpn.api.domain.entity.converter.LocaleConverter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "UPDATE service_group SET deleted = true WHERE id=?")
@Where(clause = "deleted=false")
public class ServiceGroup {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String description;

  @Column
  @Convert(converter = LocaleConverter.class)
  private Locale language;

  @Column
  @DecimalMin(value = "0.0")
  @DecimalMax(value = "100", inclusive = false)
  private BigDecimal discount;

  @ManyToMany(fetch = FetchType.LAZY)
  private List<Gateway> gateways;

  @ManyToMany
  private List<Geolocation> allowedGeolocations;

  @ManyToMany
  private List<Geolocation> disAllowedGeolocations;

  @OneToMany(mappedBy = "serviceGroup", cascade = CascadeType.ALL)
  private List<Group> groups;

  @Column
  private boolean deleted;

  @Column
  @CreatedDate
  private LocalDateTime createdAt;

  @Column
  @LastModifiedDate
  private LocalDateTime updatedAt;
}
