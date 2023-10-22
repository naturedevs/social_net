package com.orbvpn.api.resolver.query;

import static com.orbvpn.api.domain.enums.RoleName.Constants.ADMIN;

import com.orbvpn.api.domain.dto.AccountingView;
import com.orbvpn.api.domain.dto.BuyMoreLoginsView;
import com.orbvpn.api.service.AccountingService;
import graphql.kickstart.tools.GraphQLQueryResolver;
import javax.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountingQuery implements GraphQLQueryResolver {
  private final AccountingService accountingService;

  @RolesAllowed(ADMIN)
  public AccountingView accounting() {
    return accountingService.getAccounting();
  }

  public BuyMoreLoginsView getBuyMoreLogins(){ return accountingService.getBuyMoreLogins(); }
}
