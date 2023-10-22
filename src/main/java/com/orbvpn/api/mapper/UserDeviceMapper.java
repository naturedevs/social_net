package com.orbvpn.api.mapper;

import com.orbvpn.api.domain.dto.UserDevice;
import com.orbvpn.api.domain.dto.UserDeviceDto;
import com.orbvpn.api.domain.dto.UserDeviceView;
import com.orbvpn.api.domain.entity.RadCheck;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserDeviceMapper {

    @Mapping(source = "username", target = "username")
    @Mapping(source = "value", target = "deviceId")
    UserDevice userDevice(RadCheck radCheck);

    com.orbvpn.api.domain.entity.UserDevice toUserDevice(UserDeviceDto userDeviceDto);

    @Mapping(source = "user.id", target = "userId")
    UserDeviceView toUserDeviceView(com.orbvpn.api.domain.entity.UserDevice userDevice);
}
