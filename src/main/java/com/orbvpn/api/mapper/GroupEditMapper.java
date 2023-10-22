package com.orbvpn.api.mapper;

import com.orbvpn.api.domain.dto.GroupEdit;
import com.orbvpn.api.domain.entity.Group;
import com.orbvpn.api.service.ServiceGroupService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {ServiceGroupViewMapper.class, ServiceGroupService.class})
public interface GroupEditMapper {

  @Mapping(source = "serviceGroupId", target = "serviceGroup")
  Group create(GroupEdit group);

  @Mapping(source = "serviceGroupId", target = "serviceGroup")
  Group edit(@MappingTarget Group group, GroupEdit groupEdit);
}
