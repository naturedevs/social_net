package com.orbvpn.api.mapper;

import com.orbvpn.api.domain.dto.ClientServerView;
import com.orbvpn.api.domain.dto.ServerView;
import com.orbvpn.api.domain.entity.Server;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ServerViewMapper {
  ServerView toView(Server server);

  ClientServerView toClientView(Server server);
}
