package com.orbvpn.api.resolver.query;

import com.orbvpn.api.domain.dto.NewsView;
import com.orbvpn.api.service.NewsService;
import graphql.kickstart.tools.GraphQLQueryResolver;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NewsQuery implements GraphQLQueryResolver {

  private final NewsService newsService;

  public List<NewsView> news() {
    return newsService.getNews();
  }

  public NewsView newsById(int id) {
    return newsService.getNews(id);
  }
}
