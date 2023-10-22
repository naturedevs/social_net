package com.orbvpn.api.resolver.query;

import com.orbvpn.api.domain.dto.FileView;
import com.orbvpn.api.service.FileService;
import graphql.kickstart.tools.GraphQLQueryResolver;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FileQuery implements GraphQLQueryResolver {

  private final FileService fileService;

  public List<FileView> files() {
    return fileService.getFiles();
  }

  public FileView file(int id) {
    return fileService.getFile(id);
  }
}
