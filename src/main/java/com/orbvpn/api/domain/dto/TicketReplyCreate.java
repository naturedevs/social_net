package com.orbvpn.api.domain.dto;

import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TicketReplyCreate {
  @NotBlank
  private String text;
}
