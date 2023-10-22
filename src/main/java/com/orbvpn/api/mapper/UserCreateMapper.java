package com.orbvpn.api.mapper;

import com.orbvpn.api.domain.dto.UserCreate;
import com.orbvpn.api.domain.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface UserCreateMapper {
  User createEntity(UserCreate userCreate);
}
