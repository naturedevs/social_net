package com.orbvpn.api.resolver.mutation;

import com.orbvpn.api.domain.dto.InvoicePDF;
import com.orbvpn.api.domain.dto.InvoiceUpdate;
import com.orbvpn.api.domain.entity.Invoice;
import com.orbvpn.api.service.InvoicePDFService;
import com.orbvpn.api.service.InvoiceService;
import graphql.kickstart.tools.GraphQLMutationResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.annotation.security.RolesAllowed;

import static com.orbvpn.api.domain.enums.RoleName.Constants.ADMIN;
import static com.orbvpn.api.domain.enums.RoleName.Constants.RESELLER;

@Component
@RequiredArgsConstructor
@Validated
public class InvoiceMutation implements GraphQLMutationResolver {

    private final InvoiceService invoiceService;
    private final InvoicePDFService invoicePDFService;

    @RolesAllowed({ADMIN, RESELLER})
    public Invoice updateInvoice(InvoiceUpdate invoiceUpdate) {
        return invoiceService.updateInvoice(invoiceUpdate);
    }

    @RolesAllowed({ADMIN, RESELLER})
    public Boolean emailInvoicePDF(InvoicePDF invoicePDF) {
        String pdfFilename = invoicePDFService.createPDF(invoicePDF.getInvoiceId());
        if (pdfFilename == null) {
            return false;
        }
        invoicePDFService.emailPdf(pdfFilename, invoicePDF.getEmailsToSend());
        invoicePDFService.deleteAttachedFile(pdfFilename);
        return true;
    }


}
