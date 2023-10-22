package com.orbvpn.api.resolver.query;

import com.orbvpn.api.domain.dto.AccountingView;
import com.orbvpn.api.domain.dto.BuyMoreLoginsView;
import com.orbvpn.api.domain.dto.ReferralCodeView;
import com.orbvpn.api.service.AccountingService;
import com.orbvpn.api.service.ReferralCodeService;
import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.security.RolesAllowed;

import static com.orbvpn.api.domain.enums.RoleName.Constants.ADMIN;

@Component
@RequiredArgsConstructor
public class ReferralCodeQuery implements GraphQLQueryResolver {
    private final ReferralCodeService referralCodeService;

    public ReferralCodeView getReferralCode(){ return referralCodeService.getReferralCode(); }
}