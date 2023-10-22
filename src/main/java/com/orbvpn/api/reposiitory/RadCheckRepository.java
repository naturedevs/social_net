package com.orbvpn.api.reposiitory;

import com.orbvpn.api.domain.entity.RadCheck;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RadCheckRepository extends JpaRepository<RadCheck, Integer> {
  long deleteByUsername(String username);

  Optional<RadCheck> findByUsernameAndAttribute(String username, String attribute);

  void deleteByUsernameAndAttribute(String username, String attribute);

  void deleteByUsernameAndAttributeAndValue(String username, String attribute, String value);

  List<RadCheck> findByAttribute(String attribute);

  List<RadCheck> findByAttributeAndUsername(String attribute, String userName);
}
