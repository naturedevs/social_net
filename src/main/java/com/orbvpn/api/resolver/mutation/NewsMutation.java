package com.orbvpn.api.resolver.mutation;

import static com.orbvpn.api.domain.enums.RoleName.Constants.ADMIN;

import com.orbvpn.api.domain.dto.NewsEdit;
import com.orbvpn.api.domain.dto.NewsView;
import com.orbvpn.api.service.NewsService;
import graphql.kickstart.tools.GraphQLMutationResolver;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@RequiredArgsConstructor
@Validated
public class NewsMutation implements GraphQLMutationResolver {

  private final NewsService newsService;

  @RolesAllowed(ADMIN)
  public NewsView createNews(@Valid NewsEdit news) {
    return newsService.createNews(news);
  }

  @RolesAllowed(ADMIN)
  public NewsView editNews(int id,@Valid NewsEdit news) {
    return newsService.editNews(id, news);
  }

  @RolesAllowed(ADMIN)
  public NewsView deleteNews(int id) {
    return newsService.deleteNews(id);
  }
}
