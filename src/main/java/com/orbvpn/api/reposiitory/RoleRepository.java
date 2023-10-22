package com.orbvpn.api.reposiitory;

import com.orbvpn.api.domain.entity.Role;
import com.orbvpn.api.domain.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {
  Role findByName(RoleName name);
}
