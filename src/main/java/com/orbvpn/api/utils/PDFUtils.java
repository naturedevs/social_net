package com.orbvpn.api.utils;

import com.lowagie.text.DocumentException;
import com.orbvpn.api.domain.entity.Invoice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Component
@Slf4j
public class PDFUtils {

    public String createPDF(Invoice invoice, String directoryPath) throws IOException {
        String PDF_OUTPUT = directoryPath + "/Invoice_" + invoice.getId() + "_" + Timestamp.valueOf(LocalDateTime.now()).getTime() + ".pdf";

        String html = parseThymeleafTemplate(invoice);
        generatePdfFromHtml(html, PDF_OUTPUT);
        return PDF_OUTPUT;
    }

    public void generatePdfFromHtml(String html,String PDF_OUTPUT) throws IOException, DocumentException {
        OutputStream outputStream = new FileOutputStream(PDF_OUTPUT);

        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(html);
        renderer.layout();
        renderer.createPDF(outputStream);

        outputStream.close();
    }

    private String parseThymeleafTemplate(Invoice invoice) {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        Context context = new Context();
        String clientName = invoice.getClientCompanyName() != null ? invoice.getClientCompanyName() : "Client Name";
        String client_address = invoice.getAddress() != null ? invoice.getAddress() : "Client Address";
        Double groupAmount = invoice.getAmountForGroup() != null ? invoice.getAmountForGroup().doubleValue() : new BigDecimal(0).doubleValue();
        Double multiLoginAmount = invoice.getAmountForMultiLogin() != null ? invoice.getAmountForMultiLogin().doubleValue() : new BigDecimal(0).doubleValue();

        context.setVariable("client_name", clientName);
        context.setVariable("client_address",client_address);
        context.setVariable("invoice_id", invoice.getId());
        context.setVariable("invoice_date", invoice.getInvoiceDate().toLocalDate());
        context.setVariable("invoice_total", invoice.getTotalAmount().doubleValue());
        context.setVariable("group_amount", groupAmount);
        context.setVariable("multi_login_amount", multiLoginAmount);
        context.setVariable("subtotal", invoice.getTotalAmount().subtract(new BigDecimal(5)).doubleValue());
        context.setVariable("tax", "5.00");

        return templateEngine.process("invoice", context);
    }
}
