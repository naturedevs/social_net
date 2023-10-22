package com.orbvpn.api.domain.dto;

import com.orbvpn.api.domain.enums.TicketCategory;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TicketCreate {
  @NotBlank
  private String subject;
  @NotBlank
  private String text;
  @NotNull
  private TicketCategory category;
}
