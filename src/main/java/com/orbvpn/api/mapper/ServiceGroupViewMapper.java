package com.orbvpn.api.mapper;

import com.orbvpn.api.domain.dto.ServiceGroupView;
import com.orbvpn.api.domain.entity.ServiceGroup;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {GatewayViewMapper.class, GeolocationViewMapper.class, GroupViewMapper.class})
public interface ServiceGroupViewMapper {
  ServiceGroupView toView(ServiceGroup serviceGroup);
}
