package com.orbvpn.api.service.reseller;

import com.orbvpn.api.domain.entity.Group;
import com.orbvpn.api.domain.entity.Reseller;
import com.orbvpn.api.domain.entity.ResellerSale;
import com.orbvpn.api.domain.entity.User;
import com.orbvpn.api.reposiitory.ResellerSaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResellerSaleService {

    private final ResellerSaleRepository resellerSaleRepository;

    public void createSale(Reseller reseller, User user, Group group, BigDecimal price) {

        ResellerSale sale = ResellerSale.builder()
                .reseller(reseller)
                .user(user)
                .group(group)
                .price(price)
                .build();

        resellerSaleRepository.save(sale);
    }

    public List<ResellerSale> getTotalSaleOfReseller(int resellerId) {
        return resellerSaleRepository.getTotalSalesOfReseller(resellerId);
    }

    public List<ResellerSale> getLastMonthSalesOfReseller(int resellerId) {
        LocalDateTime lastMonthDate = LocalDateTime.now().minusMonths(1);
        return resellerSaleRepository.getSalesOfResellerByDate(resellerId, lastMonthDate);
    }

    public int getMonthlySalesOfReseller(int resellerId) {
        //TODO fix here
        return getTotalSaleOfReseller(resellerId).size();
    }
}
