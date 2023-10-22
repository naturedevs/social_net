package com.orbvpn.api.mapper;

import com.orbvpn.api.domain.dto.NewsView;
import com.orbvpn.api.domain.entity.News;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring", uses = RoleViewMapper.class)
public interface NewsViewMapper {

  @Mappings({
    @Mapping(source = "sendMail", target = "notifyByEmail"),
  })
  NewsView toView(News news);
}
