package com.orbvpn.api.mapper;

import com.orbvpn.api.domain.dto.GatewayView;
import com.orbvpn.api.domain.entity.Gateway;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GatewayViewMapper {

  GatewayView toView(Gateway gateway);
}
