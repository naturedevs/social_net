package com.orbvpn.api.domain.dto;

import com.orbvpn.api.domain.entity.Invoice;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class InvoiceUpdate {

    private int id;
    private String clientCompanyName;
    private String address;
    private String email;
    private String phoneNumber;
    private String taxId;
    private String companyRegistrationNumber;

    public Invoice updateInvoice(Invoice invoice) {
        if(getClientCompanyName() != null) {
            invoice.setClientCompanyName(getClientCompanyName());
        }

        if(getAddress() != null) {
            invoice.setAddress(getAddress());
        }

        if(getEmail() != null) {
            invoice.setEmail(getEmail());
        }

        if(getPhoneNumber() != null) {
            invoice.setPhoneNumber(getPhoneNumber());
        }

        if(getTaxId() != null) {
            invoice.setTaxId(getTaxId());
        }

        if(getCompanyRegistrationNumber() != null) {
            invoice.setCompanyRegistrationNumber(getCompanyRegistrationNumber());
        }
        return invoice;
    }
}
