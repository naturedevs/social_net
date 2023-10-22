package com.orbvpn.api.mapper;

import com.orbvpn.api.domain.dto.UserProfileView;
import com.orbvpn.api.domain.entity.UserProfile;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = UserProfileViewMapper.class)
public interface UserProfileViewMapper {
  UserProfileView toView(UserProfile userProfile);
}
