package com.orbvpn.api.mapper;

import com.orbvpn.api.domain.dto.UserProfileEdit;
import com.orbvpn.api.domain.entity.UserProfile;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserProfileEditMapper {
  UserProfile edit(@MappingTarget UserProfile userProfile, UserProfileEdit userProfileEdit);
}
