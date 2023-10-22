package com.orbvpn.api.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ResellerScoreDto {
    private Double resellerScore;
    private int resellerId;
}
