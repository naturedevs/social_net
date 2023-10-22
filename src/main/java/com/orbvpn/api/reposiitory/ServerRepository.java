package com.orbvpn.api.reposiitory;

import com.orbvpn.api.domain.dto.ClientServerView;
import com.orbvpn.api.domain.entity.Server;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface ServerRepository extends JpaRepository<Server, Integer> {
    Server findByPrivateIp(String privateIp);
    Server findByCryptoFriendly(int cryptoFriendly);

    @Query(value = "SELECT server.* FROM radacct JOIN server ON radacct.nasipaddress = server.private_ip where radacct.username=?1 AND hide=0 GROUP BY nasipaddress order by acctupdatetime DESC", nativeQuery=true)
    Collection<Server> findServerByRecentConnection(String Email);
}
