package com.orbvpn.api.reposiitory;

import com.orbvpn.api.domain.entity.Reseller;
import com.orbvpn.api.domain.entity.ResellerAddCredit;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ResellerAddCreditRepository extends JpaRepository<ResellerAddCredit, Integer> {
  List<ResellerAddCredit> findAllByCreatedAtAfter(LocalDateTime createdAt);

  @Query("select sum(cr.credit) from ResellerAddCredit cr where cr.reseller = :reseller and cr.createdAt > :date")
  BigDecimal getResellerCreditAfterDate(Reseller reseller, LocalDateTime date);

  @Query("select sum(cr.credit) from ResellerAddCredit cr where cr.reseller.level <> 'OWNER' and cr.createdAt > :date")
  BigDecimal getAllResellersTotalCreditAfterDate(LocalDateTime date);

  @Query("select count(cr.id) from ResellerAddCredit cr where cr.reseller = :reseller")
  BigDecimal countResellerDeposits(Reseller reseller);
}
