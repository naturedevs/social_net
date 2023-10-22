package com.orbvpn.api.domain.dto;

import static com.orbvpn.api.domain.ValidationProperties.BAD_PASSWORD_MESSAGE;
import static com.orbvpn.api.domain.ValidationProperties.PASSWORD_PATTERN;

import javax.persistence.Transient;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
public class UserCreate {
  @Email
  private String email;

  @Pattern(regexp = PASSWORD_PATTERN, message = BAD_PASSWORD_MESSAGE)
  private String password;

  private Integer resellerId;
}
