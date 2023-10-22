package com.orbvpn.api.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OAuthToken {

    private String access_token;
    private int expires_in;
    private String scope;
    private String token_type;
    private String id_token;

}
