package com.orbvpn.api.resolver.query;

import com.orbvpn.api.domain.dto.GeolocationView;
import com.orbvpn.api.service.GeolocationService;
import graphql.kickstart.tools.GraphQLQueryResolver;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GeolocationQuery implements GraphQLQueryResolver {
  private final GeolocationService geolocationService;

  List<GeolocationView> geolocations() {
    return geolocationService.getGeolocations();
  }
}
