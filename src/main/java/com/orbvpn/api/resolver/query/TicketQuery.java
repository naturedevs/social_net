package com.orbvpn.api.resolver.query;

import com.orbvpn.api.domain.dto.TicketView;
import com.orbvpn.api.domain.enums.TicketCategory;
import com.orbvpn.api.domain.enums.TicketStatus;
import com.orbvpn.api.service.HelpCenterService;
import graphql.kickstart.tools.GraphQLQueryResolver;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TicketQuery implements GraphQLQueryResolver {
  private final HelpCenterService helpCenterService;

  public Page<TicketView> tickets(int page, int size, TicketCategory category, TicketStatus status) {
    return helpCenterService.getTickets(page, size, category, status);
  }

  public List<TicketView> userTickets() {
    return helpCenterService.getUserTickets();
  }

  public TicketView ticket(int id) {
    return helpCenterService.getTicketView(id);
  }
}
