package com.orbvpn.api.resolver.mutation;

import static com.orbvpn.api.domain.enums.RoleName.Constants.ADMIN;

import com.orbvpn.api.domain.dto.GroupEdit;
import com.orbvpn.api.domain.dto.GroupView;
import com.orbvpn.api.service.GroupService;
import graphql.kickstart.tools.GraphQLMutationResolver;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@RequiredArgsConstructor
@Validated
public class GroupMutation implements GraphQLMutationResolver {

  private final GroupService groupService;

  @RolesAllowed(ADMIN)
  GroupView createGroup(@Valid GroupEdit group) {
    return groupService.createGroup(group);
  }

  @RolesAllowed(ADMIN)
  GroupView editGroup(int id, @Valid GroupEdit group) {
    return groupService.editGroup(id, group);
  }

  @RolesAllowed(ADMIN)
  GroupView deleteGroup(int id) {
    return groupService.deleteGroup(id);
  }
}
