package com.orbvpn.api.mapper;

import com.orbvpn.api.domain.dto.CouponCodeDto;
import com.orbvpn.api.domain.entity.CouponCode;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CouponCodeMapper {

    CouponCode toCouponCode(CouponCodeDto couponCodeDto);
}
