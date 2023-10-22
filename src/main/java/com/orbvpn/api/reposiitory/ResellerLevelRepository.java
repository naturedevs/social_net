package com.orbvpn.api.reposiitory;

import com.orbvpn.api.domain.entity.ResellerLevel;
import com.orbvpn.api.domain.enums.ResellerLevelName;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResellerLevelRepository extends JpaRepository<ResellerLevel, Integer> {
  ResellerLevel getByName(ResellerLevelName name);
}
