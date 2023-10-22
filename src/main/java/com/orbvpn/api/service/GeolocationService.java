package com.orbvpn.api.service;

import com.orbvpn.api.domain.dto.GeolocationView;
import com.orbvpn.api.domain.entity.Geolocation;
import com.orbvpn.api.mapper.GeolocationViewMapper;
import com.orbvpn.api.reposiitory.GeolocationRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GeolocationService {

  private final GeolocationRepository geolocationRepository;
  private final GeolocationViewMapper geolocationViewMapper;

  public List<GeolocationView> getGeolocations() {
    return geolocationRepository.findAll()
      .stream()
      .map(geolocationViewMapper::toView)
      .collect(Collectors.toList());
  }

  public List<Geolocation> findAllById(List<Integer> ids) {
    return geolocationRepository.findAllById(ids);
  }
}
