package com.orbvpn.api.reposiitory;

import com.orbvpn.api.domain.entity.Reseller;
import com.orbvpn.api.domain.entity.User;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ResellerRepository extends JpaRepository<Reseller, Integer> {
  List<Reseller> findAllByEnabled(boolean enabled);

  Optional<Reseller> findResellerByUser(User user);

  List<Reseller> findByLevelSetDateBefore(LocalDateTime levelSetDate);

  @Query("select sum(r.credit) from Reseller r where r.level <> 'OWNER'")
  BigDecimal getResellersTotalCredit();

  void deleteAllByUser(User user);
}
