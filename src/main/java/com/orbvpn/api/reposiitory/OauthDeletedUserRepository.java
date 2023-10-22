package com.orbvpn.api.reposiitory;

import com.orbvpn.api.domain.entity.OauthDeletedUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OauthDeletedUserRepository extends JpaRepository<OauthDeletedUser, Integer> {

}
