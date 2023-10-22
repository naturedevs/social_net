package com.orbvpn.api.resolver.mutation;

import static com.orbvpn.api.domain.enums.RoleName.Constants.ADMIN;

import com.orbvpn.api.domain.dto.ServerEdit;
import com.orbvpn.api.domain.dto.ServerView;
import com.orbvpn.api.service.ServerService;
import graphql.kickstart.tools.GraphQLMutationResolver;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@RequiredArgsConstructor
@Validated
public class ServerMutation implements GraphQLMutationResolver {
  private final ServerService serverService;

  @RolesAllowed(ADMIN)
  public ServerView createServer(@Valid ServerEdit server) {
    return serverService.createServer(server);
  }

  @RolesAllowed(ADMIN)
  public ServerView editServer(int id, @Valid ServerEdit serverEdit) {
    return serverService.editServer(id, serverEdit);
  }

  @RolesAllowed(ADMIN)
  public ServerView deleteServer(int id) {
    return serverService.deleteServer(id);
  }
}
