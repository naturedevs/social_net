package com.orbvpn.api.resolver.query;

import static com.orbvpn.api.domain.enums.RoleName.Constants.ADMIN;

import com.orbvpn.api.domain.dto.ClientServerView;
import com.orbvpn.api.domain.dto.ServerView;
import com.orbvpn.api.service.ServerService;
import graphql.kickstart.tools.GraphQLQueryResolver;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ServerQuery implements GraphQLQueryResolver {
  private final ServerService serverService;

  @RolesAllowed(ADMIN)
  public List<ServerView> servers() {
    return serverService.getServers();
  }

  public List<ClientServerView> clientServers() {
    return serverService.getClientServers();
  }

  public List<ClientServerView> getClientSortedServers(String sortBy, String parameter) {return serverService.getClientSortedServers(sortBy, parameter);}

  @RolesAllowed(ADMIN)
  public ServerView server(int id) {
    return serverService.getServer(id);
  }

}
