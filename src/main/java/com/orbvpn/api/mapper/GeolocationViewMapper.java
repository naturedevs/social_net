package com.orbvpn.api.mapper;

import com.orbvpn.api.domain.dto.GeolocationView;
import com.orbvpn.api.domain.entity.Geolocation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GeolocationViewMapper {
  GeolocationView toView(Geolocation geolocation);
}
