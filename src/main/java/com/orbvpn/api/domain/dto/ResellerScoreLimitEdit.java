package com.orbvpn.api.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResellerScoreLimitEdit {

    private String symbol;
    private Integer maxLimit;
}
