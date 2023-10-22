package com.orbvpn.api.mapper;

import com.orbvpn.api.domain.dto.TicketCreate;
import com.orbvpn.api.domain.entity.Ticket;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TicketEditMapper {
  Ticket create(TicketCreate ticketCreate);
}
