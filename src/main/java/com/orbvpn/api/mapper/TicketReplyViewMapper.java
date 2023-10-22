package com.orbvpn.api.mapper;

import com.orbvpn.api.domain.dto.TicketReplyView;
import com.orbvpn.api.domain.entity.TicketReply;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {UserViewMapper.class})
public interface TicketReplyViewMapper {
  TicketReplyView toView(TicketReply ticket);
}
