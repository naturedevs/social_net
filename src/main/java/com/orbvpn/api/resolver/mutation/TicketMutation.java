package com.orbvpn.api.resolver.mutation;

import static com.orbvpn.api.domain.enums.RoleName.Constants.ADMIN;

import com.orbvpn.api.domain.dto.TicketCreate;
import com.orbvpn.api.domain.dto.TicketReplyCreate;
import com.orbvpn.api.domain.dto.TicketReplyView;
import com.orbvpn.api.domain.dto.TicketView;
import com.orbvpn.api.service.HelpCenterService;
import graphql.kickstart.tools.GraphQLMutationResolver;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@RequiredArgsConstructor
@Validated
public class TicketMutation implements GraphQLMutationResolver {

  private final HelpCenterService helpCenterService;

  public TicketView createTicket(TicketCreate ticket)  {
    return helpCenterService.createTicket(ticket);
  }


  public TicketView closeTicket(int id) {
    return helpCenterService.closeTicket(id);
  }

  @RolesAllowed(ADMIN)
  public List<TicketView> closeTickets(List<Integer> ids) {
    return helpCenterService.closeTickets(ids);
  }

  public TicketReplyView replyToTicket(int ticketId, TicketReplyCreate reply) {
    return helpCenterService.replyToTicket(ticketId, reply);
  }
}
