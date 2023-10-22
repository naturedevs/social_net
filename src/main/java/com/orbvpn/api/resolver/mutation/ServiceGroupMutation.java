package com.orbvpn.api.resolver.mutation;

import static com.orbvpn.api.domain.enums.RoleName.Constants.ADMIN;

import com.orbvpn.api.domain.dto.ServiceGroupEdit;
import com.orbvpn.api.domain.dto.ServiceGroupView;
import com.orbvpn.api.service.ServiceGroupService;
import graphql.kickstart.tools.GraphQLMutationResolver;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@RequiredArgsConstructor
@Validated
public class ServiceGroupMutation implements GraphQLMutationResolver {

  private final ServiceGroupService serviceGroupService;

  @RolesAllowed(ADMIN)
  ServiceGroupView createServiceGroup(@Valid ServiceGroupEdit serviceGroup) {
    return serviceGroupService.createServiceGroup(serviceGroup);
  }

  @RolesAllowed(ADMIN)
  ServiceGroupView editServiceGroup(int id, @Valid ServiceGroupEdit serviceGroupEdit) {
    return serviceGroupService.editServiceGroup(id, serviceGroupEdit);
  }

  @RolesAllowed(ADMIN)
  ServiceGroupView deleteServiceGroup(int id) {
    return serviceGroupService.deleteServiceGroup(id);
  }
}
