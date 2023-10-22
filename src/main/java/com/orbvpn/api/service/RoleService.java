package com.orbvpn.api.service;

import com.orbvpn.api.domain.entity.Role;
import com.orbvpn.api.domain.enums.RoleName;
import com.orbvpn.api.reposiitory.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {

  private final RoleRepository roleRepository;

  public Role getByName(RoleName roleName) {
    return roleRepository.findByName(roleName);
  }

  public List<Role> findAllById(List<Integer> id) {
    return roleRepository.findAllById(id);
  }

  public List<Role> findAll() {
    return roleRepository.findAll();
  }
}
