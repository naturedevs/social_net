package com.orbvpn.api.mapper;

import com.orbvpn.api.domain.dto.ResellerView;
import com.orbvpn.api.domain.entity.Reseller;
import com.orbvpn.api.domain.entity.ResellerLevel;
import com.orbvpn.api.domain.enums.ResellerLevelName;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring", uses = ServiceGroupViewMapper.class)
public interface ResellerViewMapper {
  @Mappings({
    @Mapping(source = "user.email", target = "email"),
  })
  ResellerView toView(Reseller reseller);

  default ResellerLevelName mapResellerLeveLName(ResellerLevel resellerLevel) {
    return resellerLevel.getName();
  }
}
