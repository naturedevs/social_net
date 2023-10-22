package com.orbvpn.api.reposiitory;

import com.orbvpn.api.domain.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {

    @Query("select invoice from Invoice invoice where invoice.payment.id = :paymentId")
    Optional<Invoice> findByPaymentId(int paymentId);

    @Query("select invoice from Invoice invoice where invoice.invoiceDate >= :beginDate and invoice.invoiceDate <= :endDate")
    List<Invoice> getByDateRange(LocalDateTime beginDate, LocalDateTime endDate);
}
