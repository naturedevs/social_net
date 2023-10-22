package com.orbvpn.api.domain.dto;

import static com.orbvpn.api.domain.ValidationProperties.BAD_PASSWORD_MESSAGE;
import static com.orbvpn.api.domain.ValidationProperties.PASSWORD_PATTERN;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResellerUserCreate {
  @Email
  private String email;

  @Pattern(regexp = PASSWORD_PATTERN, message = BAD_PASSWORD_MESSAGE)
  private String password;

  @Positive
  private int groupId;

}
