package com.orbvpn.api.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TwitterUserInfo {

    private String name;
    private String email;
    private String screen_name;
    private String location;
}
