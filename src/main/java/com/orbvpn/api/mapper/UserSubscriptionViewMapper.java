package com.orbvpn.api.mapper;

import com.orbvpn.api.domain.dto.UserSubscriptionView;
import com.orbvpn.api.domain.entity.UserSubscription;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = GroupViewMapper.class)
public interface UserSubscriptionViewMapper {
  UserSubscriptionView toView(UserSubscription userSubscription);
}
