package com.orbvpn.api.resolver.query;

import com.orbvpn.api.config.security.Unsecured;
import com.orbvpn.api.domain.dto.GroupView;
import com.orbvpn.api.service.GroupService;
import graphql.kickstart.tools.GraphQLQueryResolver;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GroupQuery implements GraphQLQueryResolver {
  private final GroupService groupService;

  List<GroupView> groups(int serviceGroupId) {
    return groupService.getGroups(serviceGroupId);
  }

  @Unsecured
  List<GroupView> registrationGroups() {
    return groupService.getRegistrationGroups();
  }

  List<GroupView> allGroups() {
    return groupService.getAllGroups();
  }

  GroupView group(int id) {
    return groupService.getGroup(id);
  }
}
