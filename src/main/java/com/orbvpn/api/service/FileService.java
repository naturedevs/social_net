package com.orbvpn.api.service;

import com.orbvpn.api.domain.dto.FileEdit;
import com.orbvpn.api.domain.dto.FileView;
import com.orbvpn.api.domain.entity.File;
import com.orbvpn.api.domain.entity.News;
import com.orbvpn.api.domain.entity.Role;
import com.orbvpn.api.domain.enums.RoleName;
import com.orbvpn.api.exception.AccessDeniedException;
import com.orbvpn.api.exception.NotFoundException;
import com.orbvpn.api.mapper.FileEditMapper;
import com.orbvpn.api.mapper.FileViewMapper;
import com.orbvpn.api.reposiitory.FileRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

  private final UserService userService;
  private final FileRepository fileRepository;
  private final FileEditMapper fileEditMapper;
  private final FileViewMapper fileViewMapper;

  public List<FileView> getFiles() {
    Role currentUserRole = userService.getUserRole();

    return fileRepository.findAll()
      .stream()
      .filter(it -> currentUserRole.getName() == RoleName.ADMIN || it.getRoles()
        .contains(currentUserRole))
      .map(fileViewMapper::toView)
      .collect(Collectors.toList());
  }

  public FileView getFile(int id) {
    Role currentUserRole = userService.getUserRole();
    File file = getById(id);

    if (currentUserRole.getName() != RoleName.ADMIN && !file.getRoles().contains(currentUserRole)) {
      throw new AccessDeniedException(News.class);
    }

    return fileViewMapper.toView(file);
  }

  public FileView createFile(FileEdit fileEdit) {
    log.info("Creating file {}", fileEdit);

    File file = fileEditMapper.create(fileEdit);
    fileRepository.save(file);
    FileView fileView = fileViewMapper.toView(file);

    log.info("Created file {}", fileView);

    return fileView;
  }

  public FileView editFile(int id, FileEdit fileEdit) {
    log.info("Editing file with id {} with data {}", id, fileEdit);

    File file = getById(id);
    fileEditMapper.edit(file, fileEdit);
    fileRepository.save(file);
    FileView fileView = fileViewMapper.toView(file);

    log.info("Updated file to data {}", fileView);

    return fileView;
  }

  public FileView deleteFile(int id) {
    log.info("Deleting file with id {}", id);

    File file = getById(id);
    fileRepository.delete(file);
    FileView fileView = fileViewMapper.toView(file);

    log.info("Deleted file with id: {}", id);
    return fileView;
  }

  private File getById(int id) {
    return fileRepository.findById(id)
      .orElseThrow(
        () -> new NotFoundException(FileEdit.class, id));
  }
}
