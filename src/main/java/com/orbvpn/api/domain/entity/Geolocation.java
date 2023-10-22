package com.orbvpn.api.domain.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Geolocation {
  @Id
  private int id;

  @Column
  private String name;

  @Column
  private String code;

  @Column
  private String threeCharCode;
}
