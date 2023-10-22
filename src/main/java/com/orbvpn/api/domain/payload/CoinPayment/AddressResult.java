package com.orbvpn.api.domain.payload.CoinPayment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddressResult {
    private String address;
    private String pubkey;
    private int dest_tag;
}
