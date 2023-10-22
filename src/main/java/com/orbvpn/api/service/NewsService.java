package com.orbvpn.api.service;

import com.orbvpn.api.domain.dto.NewsEdit;
import com.orbvpn.api.domain.dto.NewsView;
import com.orbvpn.api.domain.entity.News;
import com.orbvpn.api.domain.entity.Role;
import com.orbvpn.api.domain.enums.RoleName;
import com.orbvpn.api.exception.AccessDeniedException;
import com.orbvpn.api.exception.NotFoundException;
import com.orbvpn.api.mapper.NewsEditMapper;
import com.orbvpn.api.mapper.NewsViewMapper;
import com.orbvpn.api.reposiitory.NewsRepository;
import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NewsService {

  private final UserService userService;
  private final NewsRepository newsRepository;
  private final NewsEditMapper newsEditMapper;
  private final NewsViewMapper newsViewMapper;

  public List<NewsView> getNews() {
    Role currentUserRole = userService.getUserRole();

    return newsRepository.findAll()
      .stream()
      .filter(it -> currentUserRole.getName() == RoleName.ADMIN || it.getRoles()
        .contains(currentUserRole))
      .map(newsViewMapper::toView)
      .collect(Collectors.toList());
  }

  public NewsView getNews(int id) {
    Role currentUserRole = userService.getUserRole();
    News news = getById(id);

    if (currentUserRole.getName() != RoleName.ADMIN && !news.getRoles().contains(currentUserRole)) {
      throw new AccessDeniedException(News.class);
    }

    return newsViewMapper.toView(news);
  }

  public NewsView createNews(NewsEdit newsEdit) {
    log.info("Creating news {}", newsEdit);

    News news = newsEditMapper.create(newsEdit);
    newsRepository.save(news);
    NewsView newsView = newsViewMapper.toView(news);

    log.info("Created news {}", newsView);

    return newsView;
  }

  public NewsView editNews(int id, NewsEdit newsEdit) {
    log.info("Editing news with id {} with data {}", id, newsEdit);

    News oldNews = getById(id);
    News news = newsEditMapper.edit(oldNews, newsEdit);
    newsRepository.save(news);
    NewsView newsView = newsViewMapper.toView(oldNews);

    log.info("Updated news to data {}", newsView);

    return newsView;
  }

  public NewsView deleteNews(int id) {
    log.info("Deleting news with id {}", id);

    News news = getById(id);
    newsRepository.delete(news);
    NewsView newsView = newsViewMapper.toView(news);

    log.info("Deleted news with id: {}", id);
    return newsView;
  }

  private News getById(int id) {
    return newsRepository.findById(id)
      .orElseThrow(
        () -> new NotFoundException(MessageFormat.format("Not found news with id: {0}", id)));
  }

}
