package com.orbvpn.api.resolver.query;

import com.orbvpn.api.domain.dto.ResellerCreditView;
import com.orbvpn.api.domain.dto.ResellerLevelCoefficientsView;
import com.orbvpn.api.domain.dto.ResellerLevelView;
import com.orbvpn.api.domain.dto.ResellerView;
import com.orbvpn.api.domain.entity.ResellerScoreLimit;
import com.orbvpn.api.service.reseller.ResellerScoreService;
import com.orbvpn.api.service.reseller.ResellerService;
import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.security.RolesAllowed;
import java.math.BigDecimal;
import java.util.List;

import static com.orbvpn.api.domain.enums.RoleName.Constants.ADMIN;

@Component
@RequiredArgsConstructor
public class ResellerQuery implements GraphQLQueryResolver {

  private final ResellerService resellerService;
  private final ResellerScoreService resellerScoreService;

  @RolesAllowed(ADMIN)
  public ResellerView reseller(int id) {
    return resellerService.getReseller(id);
  }

  @RolesAllowed(ADMIN)
  public List<ResellerView> resellers() {
    return resellerService.getEnabledResellers();
  }

  @RolesAllowed(ADMIN)
  public BigDecimal totalResellersCredit() {
    return resellerService.getTotalResellersCredit();
  }

  @RolesAllowed(ADMIN)
  public List<ResellerLevelView> getResellersLevels() {
    return resellerService.getResellersLevels();
  }

  public List<ResellerCreditView> getResellersCredits() {
    return resellerService.getResellersCredits();
  }

  @RolesAllowed(ADMIN)
  public ResellerLevelCoefficientsView getResellerLevelCoefficients() {
    return resellerService.getResellerLevelCoefficients();
  }

  @RolesAllowed(ADMIN)
  public List<ResellerScoreLimit> getResellerScoreLimits() {
    return resellerScoreService.getScoreLimits();
  }

}
