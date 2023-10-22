package com.orbvpn.api.reposiitory;

import com.orbvpn.api.domain.entity.ResellerSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ResellerSaleRepository extends JpaRepository<ResellerSale, Integer> {

    @Query(value = "SELECT * FROM reseller_sale where reseller_id=:resellerId", nativeQuery=true)
    List<ResellerSale> getTotalSalesOfReseller(int resellerId);

    @Query(value = "SELECT * FROM reseller_sale where reseller_id=:resellerId and created_at >= :date", nativeQuery=true)
    List<ResellerSale> getSalesOfResellerByDate(int resellerId, LocalDateTime date);
}
