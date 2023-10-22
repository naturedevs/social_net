package com.orbvpn.api.mapper;

import com.orbvpn.api.domain.dto.FileEdit;
import com.orbvpn.api.domain.entity.File;
import com.orbvpn.api.service.RoleService;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {RoleService.class})
public interface FileEditMapper {

  File create(FileEdit fileEdit);

  File edit(@MappingTarget File file, FileEdit fileEdit);
}
