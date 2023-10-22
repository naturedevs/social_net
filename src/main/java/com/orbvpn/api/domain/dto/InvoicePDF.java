package com.orbvpn.api.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class InvoicePDF {

    private int invoiceId;
    private List<String> emailsToSend;
}
