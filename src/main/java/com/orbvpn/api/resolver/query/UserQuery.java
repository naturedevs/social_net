package com.orbvpn.api.resolver.query;

import com.orbvpn.api.domain.dto.UserProfileView;
import com.orbvpn.api.domain.dto.UserSubscriptionView;
import com.orbvpn.api.domain.dto.UserView;
import com.orbvpn.api.service.UserService;
import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserQuery implements GraphQLQueryResolver  {
  private final UserService userService;

  public UserView user() {
    return userService.getUserView();
  }

  public UserProfileView userProfile() {
    return userService.getProfile();
  }

  public UserSubscriptionView userSubscription() {
    return userService.getUserSubscription();
  }
}
