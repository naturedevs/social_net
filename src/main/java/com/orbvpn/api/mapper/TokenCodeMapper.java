package com.orbvpn.api.mapper;

import com.orbvpn.api.domain.dto.TokenCodeView;
import com.orbvpn.api.domain.entity.TokenCode;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)

public interface TokenCodeMapper {

    TokenCodeView toTokenCodeView(TokenCode tokenCode);
}
