package com.orbvpn.api.reposiitory;

import com.orbvpn.api.domain.entity.StripeCustomer;
import com.orbvpn.api.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StripeCustomerRepository extends JpaRepository<StripeCustomer, Integer> {
  StripeCustomer findByUser(User user);
}
