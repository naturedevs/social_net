package com.orbvpn.api.mapper;

import com.orbvpn.api.domain.dto.FileView;
import com.orbvpn.api.domain.entity.File;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = RoleViewMapper.class)
public interface FileViewMapper {

  FileView toView(File file);
}
