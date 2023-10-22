package com.orbvpn.api.resolver.mutation;

import com.orbvpn.api.domain.dto.TokenCodeDto;
import com.orbvpn.api.domain.dto.TokenCodeResponse;
import com.orbvpn.api.domain.dto.TokenCodeView;
import com.orbvpn.api.service.TokenCodeService;
import graphql.kickstart.tools.GraphQLMutationResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.security.RolesAllowed;

import static com.orbvpn.api.domain.enums.RoleName.Constants.ADMIN;

@Component
@RequiredArgsConstructor
public class TokenCodeMutation implements GraphQLMutationResolver {

    private final TokenCodeService tokenCodeService;

    @RolesAllowed(ADMIN)
    public TokenCodeResponse generateTokenCodeForUser(TokenCodeDto tokenCodeDto) {
        return tokenCodeService.generateTokenCodeForUser(tokenCodeDto);
    }

    public TokenCodeView useTokenCode(String code) {
        return tokenCodeService.useTokenCode(code);
    }

    public TokenCodeView checkTokenCode(String code) {
        return tokenCodeService.checkTokenCode(code);
    }
}
