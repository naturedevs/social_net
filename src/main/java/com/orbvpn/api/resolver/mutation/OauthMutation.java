package com.orbvpn.api.resolver.mutation;

import com.orbvpn.api.config.security.Unsecured;
import com.orbvpn.api.domain.dto.AuthenticatedUser;
import com.orbvpn.api.domain.enums.SocialMedia;
import com.orbvpn.api.service.social_login.OauthService;
import graphql.kickstart.tools.GraphQLMutationResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Component
public class OauthMutation implements GraphQLMutationResolver {
  private final OauthService oauthService;

  @Unsecured
  public AuthenticatedUser oauthLogin(String token, SocialMedia socialMedia) {
    return oauthService.oauthLogin(token, socialMedia);
  }
}
