package com.orbvpn.api.resolver.mutation;

import static com.orbvpn.api.domain.enums.RoleName.Constants.ADMIN;

import com.orbvpn.api.domain.dto.FileEdit;
import com.orbvpn.api.domain.dto.FileView;
import com.orbvpn.api.service.FileService;
import graphql.kickstart.tools.GraphQLMutationResolver;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@RequiredArgsConstructor
@Validated
public class FileMutation implements GraphQLMutationResolver {

  private final FileService fileService;

  @RolesAllowed(ADMIN)
  public FileView createFile(@Valid FileEdit file) {
    return fileService.createFile(file);
  }

  @RolesAllowed(ADMIN)
  public FileView editFile(int id, @Valid FileEdit file) {
    return fileService.editFile(id, file);
  }

  @RolesAllowed(ADMIN)
  public FileView deleteFile(int id) {
    return fileService.deleteFile(id);
  }
}
