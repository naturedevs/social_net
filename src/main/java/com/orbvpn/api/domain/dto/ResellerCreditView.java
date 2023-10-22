package com.orbvpn.api.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ResellerCreditView {
    public int id;
    public String email;
    public BigDecimal credit;
}
