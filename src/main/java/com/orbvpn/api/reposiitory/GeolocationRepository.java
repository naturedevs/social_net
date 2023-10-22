package com.orbvpn.api.reposiitory;

import com.orbvpn.api.domain.entity.Geolocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GeolocationRepository extends JpaRepository<Geolocation, Integer> {

}
