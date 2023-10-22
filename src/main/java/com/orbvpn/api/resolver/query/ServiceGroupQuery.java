package com.orbvpn.api.resolver.query;

import com.orbvpn.api.domain.dto.ServiceGroupView;
import com.orbvpn.api.service.ServiceGroupService;
import graphql.kickstart.tools.GraphQLQueryResolver;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ServiceGroupQuery implements GraphQLQueryResolver {

  private final ServiceGroupService serviceGroupService;

  List<ServiceGroupView> serviceGroups() {
    return serviceGroupService.getAllServiceGroups();
  }

  ServiceGroupView serviceGroup(int id) {
    return serviceGroupService.getServiceGroup(id);
  }
}
