package com.orbvpn.api.mapper;


import com.orbvpn.api.domain.dto.ResellerLevelView;
import com.orbvpn.api.domain.entity.ResellerLevel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ResellerLevelViewMapper {
  ResellerLevelView toView(ResellerLevel level);
}
