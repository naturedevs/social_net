package com.orbvpn.api.reposiitory;

import com.orbvpn.api.domain.entity.Nas;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NasRepository extends JpaRepository<Nas, Integer> {

  Optional<Nas> findByNasName(String nasName);
}
