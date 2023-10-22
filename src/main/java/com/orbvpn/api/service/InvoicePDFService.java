package com.orbvpn.api.service;

import com.orbvpn.api.domain.entity.Invoice;
import com.orbvpn.api.service.notification.EmailService;
import com.orbvpn.api.utils.PDFUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class InvoicePDFService {

    public static final String INVOICES_FOLDER = "/var/tmp/invoices";

    private final InvoiceService invoiceService;
    private final PDFUtils pdfUtils;
    private final EmailService emailService;
    private final MailProperties mailProperties;

    /*
        Returns filename of the created PDF
    */
    public String createPDF(int invoiceId) {
        Invoice invoice = invoiceService.getById(invoiceId);
        try {
            return pdfUtils.createPDF(invoice, INVOICES_FOLDER);
        } catch (Exception e) {
            log.error("Error creating PDF for the invoice {}.",invoiceId);
        }
        return null;
    }

    /*
        Emails given PDF file to given email addresses
    */
    public void emailPdf(String filename, List<String> emails) {

        log.info("Emailing Invoice as PDF");
        String emailTitle = "Invoice for the Payment";
        String emailMessage = "Hello!<br><br>" +
                "Thank you for choosing us.<br><br>" +
                "You can see the invoice details of your payment in the attached document!<br><br>";

        emails.forEach(
                email -> emailService.sendMail(
                            mailProperties.getUsername(),
                            email,
                            emailTitle,
                            emailMessage,
                            filename
                )
        );
    }

    /*
        Deletes PDF file after sending the mail
    */
    public void deleteAttachedFile(String filename) {
        File attachedFile = new File(filename);
        if(attachedFile.delete())
            log.info("Deleted the file {}.", filename);
        else
            log.error("Error deleting the file {}.", filename);

    }

}
