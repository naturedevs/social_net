package com.orbvpn.api.mapper;

import com.orbvpn.api.domain.dto.DeviceView;
import com.orbvpn.api.domain.entity.Device;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DeviceMapper {
    DeviceView deviceView(Device device);
}
