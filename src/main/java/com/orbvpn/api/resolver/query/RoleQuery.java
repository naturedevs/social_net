package com.orbvpn.api.resolver.query;

import com.orbvpn.api.domain.dto.RoleView;
import com.orbvpn.api.mapper.RoleViewMapper;
import com.orbvpn.api.service.RoleService;
import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RoleQuery implements GraphQLQueryResolver {
    private final RoleService roleService;
    private final RoleViewMapper roleViewMapper;

    public List<RoleView> getAllUserRoles() {
        return roleService.findAll().stream()
                .map(roleViewMapper::toView)
                .collect(Collectors.toList());
    }
}
