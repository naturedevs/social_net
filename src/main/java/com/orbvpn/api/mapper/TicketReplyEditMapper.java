package com.orbvpn.api.mapper;

import com.orbvpn.api.domain.dto.TicketReplyCreate;
import com.orbvpn.api.domain.entity.TicketReply;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TicketReplyEditMapper {
  TicketReply create(TicketReplyCreate ticketReplyCreate);
}
