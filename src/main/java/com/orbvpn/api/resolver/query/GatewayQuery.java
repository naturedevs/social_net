package com.orbvpn.api.resolver.query;

import com.orbvpn.api.domain.dto.GatewayView;
import com.orbvpn.api.service.GatewayService;
import graphql.kickstart.tools.GraphQLQueryResolver;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GatewayQuery implements GraphQLQueryResolver {

  private final GatewayService gatewayService;

  List<GatewayView> gateways() {
    return gatewayService.getAllGateways();
  }
}
