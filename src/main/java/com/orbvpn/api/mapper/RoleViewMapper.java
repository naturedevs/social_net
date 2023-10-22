package com.orbvpn.api.mapper;

import com.orbvpn.api.domain.dto.RoleView;
import com.orbvpn.api.domain.entity.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleViewMapper {
  RoleView toView(Role role);
}
