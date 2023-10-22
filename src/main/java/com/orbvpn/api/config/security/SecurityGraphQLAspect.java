package com.orbvpn.api.config.security;

import com.orbvpn.api.exception.UnauthenticatedAccessException;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(1)
public class SecurityGraphQLAspect {

  /**
   * All graphQLResolver methods can be called only by authenticated user.
   *
   * @Unsecured annotated methods are excluded
   */

  @Before("allGraphQLResolverMethods() && isDefinedInApplication() && !isMethodAnnotatedAsUnsecured()")
  public void doSecurityCheck() {
    if (SecurityContextHolder.getContext() == null ||
      SecurityContextHolder.getContext().getAuthentication() == null ||
      !SecurityContextHolder.getContext().getAuthentication().isAuthenticated() ||
      AnonymousAuthenticationToken.class
        .isAssignableFrom(SecurityContextHolder.getContext().getAuthentication().getClass())) {
      throw new UnauthenticatedAccessException("Sorry, you should log in first to do that!");
    }
  }


  /**
   * Matches all beans that implement {@link } as {@code UserMutation}, {@code UserQuery} extend.
   * GraphQLResolver interface
   */
  @Pointcut("target(graphql.kickstart.tools.GraphQLResolver)")
  private void allGraphQLResolverMethods() {
    //leave empty
  }

  /**
   * Matches all beans in com.zerofiltre.samplegraphqlerrorhandling package.
   */
  @Pointcut("within(com.orbvpn.api..*)")
  private void isDefinedInApplication() {
    //leave empty
  }

  /**
   * Any method annotated with @Unsecured.
   */
  @Pointcut("@annotation(com.orbvpn.api.config.security.Unsecured)")
  private void isMethodAnnotatedAsUnsecured() {
    //leave empty
  }

}