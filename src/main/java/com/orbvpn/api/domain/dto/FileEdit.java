package com.orbvpn.api.domain.dto;

import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FileEdit {

  @NotBlank
  private String title;

  @NotBlank
  private String description;

  @NotBlank
  private String url;

  @NotEmpty
  private List<Integer> roles;
}
