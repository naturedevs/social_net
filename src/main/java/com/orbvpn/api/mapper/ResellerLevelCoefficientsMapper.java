package com.orbvpn.api.mapper;

import com.orbvpn.api.domain.dto.ResellerLevelCoefficientsEdit;
import com.orbvpn.api.domain.dto.ResellerLevelCoefficientsView;
import com.orbvpn.api.domain.entity.ResellerLevelCoefficients;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ResellerLevelCoefficientsMapper {
  ResellerLevelCoefficients edit(ResellerLevelCoefficientsEdit resellerLevelCoefficientsEdit);

  ResellerLevelCoefficientsView toView(ResellerLevelCoefficients resellerLevelCoefficients);
}
