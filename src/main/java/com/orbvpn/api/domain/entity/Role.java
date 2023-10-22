package com.orbvpn.api.domain.entity;

import com.orbvpn.api.domain.enums.RoleName;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Role {

  @Id
  private int id;

  @Column
  @Enumerated(EnumType.STRING)
  private RoleName name;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Role role = (Role) o;
    return name == role.name;
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }
}
