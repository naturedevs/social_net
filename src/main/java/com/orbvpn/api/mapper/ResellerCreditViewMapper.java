package com.orbvpn.api.mapper;


import com.orbvpn.api.domain.dto.ResellerCreditView;
import com.orbvpn.api.domain.entity.Reseller;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ResellerCreditViewMapper {
  @Mappings({
          @Mapping(source = "user.email", target = "email"),
  })
  ResellerCreditView toView(Reseller level);
}
