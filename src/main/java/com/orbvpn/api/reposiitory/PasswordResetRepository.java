package com.orbvpn.api.reposiitory;

import com.orbvpn.api.domain.entity.PasswordReset;
import com.orbvpn.api.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetRepository extends JpaRepository<PasswordReset, String> {
    Long deleteByUserAndTokenNot(User user, String token);

    void deleteAllByUser(User user);
}
