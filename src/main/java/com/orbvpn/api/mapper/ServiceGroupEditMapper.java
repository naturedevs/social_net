package com.orbvpn.api.mapper;

import com.orbvpn.api.domain.dto.ServiceGroupEdit;
import com.orbvpn.api.domain.entity.ServiceGroup;
import com.orbvpn.api.service.GatewayService;
import com.orbvpn.api.service.GeolocationService;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {GatewayService.class, GeolocationService.class})
public interface ServiceGroupEditMapper {

  ServiceGroup create(ServiceGroupEdit serviceGroupEdit);

  ServiceGroup edit(@MappingTarget ServiceGroup serviceGroup, ServiceGroupEdit serviceGroupEdit);
}
