package com.orbvpn.api.service;

import com.orbvpn.api.domain.dto.GroupEdit;
import com.orbvpn.api.domain.dto.GroupView;
import com.orbvpn.api.domain.entity.Group;
import com.orbvpn.api.domain.entity.ServiceGroup;
import com.orbvpn.api.exception.NotFoundException;
import com.orbvpn.api.mapper.GroupEditMapper;
import com.orbvpn.api.mapper.GroupViewMapper;
import com.orbvpn.api.reposiitory.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class GroupService {

  private final GroupRepository groupRepository;
  private final GroupViewMapper groupViewMapper;
  private final GroupEditMapper groupEditMapper;
  private final ServiceGroupService serviceGroupService;

  public GroupView createGroup(GroupEdit groupEdit) {
    Group group = groupEditMapper.create(groupEdit);

    groupRepository.save(group);

    return groupViewMapper.toView(group);
  }

  public GroupView editGroup(int id, GroupEdit groupEdit) {
    Group group = getById(id);

    Group edited = groupEditMapper.edit(group, groupEdit);

    groupRepository.save(edited);

    return groupViewMapper.toView(edited);
  }

  public GroupView deleteGroup(int id) {
    Group group = getById(id);

    groupRepository.delete(group);

    return groupViewMapper.toView(group);
  }

  public List<GroupView> getRegistrationGroups() {
    return groupRepository.findAllByRegistrationGroupIsTrue()
      .stream()
      .map(groupViewMapper::toView)
      .collect(Collectors.toList());
  }

  public List<GroupView> getAllGroups() {
    return groupRepository.findAll()
      .stream()
      .map(groupViewMapper::toView)
      .collect(Collectors.toList());
  }

  public List<GroupView> getGroups(int serviceGroupId) {
    ServiceGroup serviceGroup = serviceGroupService.getById(serviceGroupId);
    return groupRepository.findAllByServiceGroup(serviceGroup)
      .stream()
      .map(groupViewMapper::toView)
      .collect(Collectors.toList());
  }

  public GroupView getGroup(int id) {
    Group group = getById(id);

    return groupViewMapper.toView(group);
  }

  public Group getById(int id) {
    return groupRepository.findById(id)
      .orElseThrow(()->new NotFoundException("Group not found"));
  }

  public Group getGroupIgnoreDelete(int id) {
    return groupRepository.getGroupIgnoreDelete(id);
  }
}
