package com.orbvpn.api.mapper;

import com.orbvpn.api.domain.dto.GroupView;
import com.orbvpn.api.domain.dto.ServiceGroupView;
import com.orbvpn.api.domain.entity.Group;
import com.orbvpn.api.domain.entity.ServiceGroup;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GroupViewMapper {

  GroupView toView(Group group);

  default ServiceGroupView toView(ServiceGroup serviceGroup) {
    ServiceGroupView serviceGroupView = new ServiceGroupView();

    serviceGroupView.setId(serviceGroup.getId());
    serviceGroupView.setName(serviceGroup.getName());
    serviceGroupView.setDescription(serviceGroup.getDescription());
    serviceGroupView.setLanguage(serviceGroup.getLanguage());
    serviceGroupView.setDiscount(serviceGroup.getDiscount());

    return serviceGroupView;
  }
}
