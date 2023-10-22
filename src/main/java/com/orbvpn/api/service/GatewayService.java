package com.orbvpn.api.service;

import com.orbvpn.api.domain.dto.GatewayView;
import com.orbvpn.api.domain.entity.Gateway;
import com.orbvpn.api.mapper.GatewayViewMapper;
import com.orbvpn.api.reposiitory.GatewayRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GatewayService {

  private final GatewayRepository gatewayRepository;
  private final GatewayViewMapper gatewayViewMapper;

  public List<GatewayView> getAllGateways() {
    return gatewayRepository.findAll()
      .stream()
      .map(gatewayViewMapper::toView)
      .collect(Collectors.toList());
  }

  public List<Gateway> findAllById(List<Integer> ids) {
    return gatewayRepository.findAllById(ids);
  }
}
