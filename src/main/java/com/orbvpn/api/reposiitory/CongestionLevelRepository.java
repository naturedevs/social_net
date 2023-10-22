package com.orbvpn.api.reposiitory;

import com.orbvpn.api.domain.dto.ClientServerView;
import com.orbvpn.api.domain.entity.CongestionLevel;
import com.orbvpn.api.domain.entity.Server;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface CongestionLevelRepository extends JpaRepository<CongestionLevel, Integer> {
}
