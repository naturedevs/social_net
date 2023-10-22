package com.orbvpn.api.mapper;

import com.orbvpn.api.domain.dto.TicketView;
import com.orbvpn.api.domain.entity.Ticket;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {UserViewMapper.class, TicketReplyViewMapper.class})
public interface TicketViewMapper {
  TicketView toView(Ticket ticket);
}
