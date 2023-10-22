package com.orbvpn.api.mapper;

import com.orbvpn.api.domain.dto.ResellerLevelEdit;
import com.orbvpn.api.domain.entity.ResellerLevel;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ResellerLevelEditMapper {
  ResellerLevel edit(@MappingTarget ResellerLevel resellerLevel,
    ResellerLevelEdit resellerLevelEdit);
}
