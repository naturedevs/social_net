package com.orbvpn.api.reposiitory;

import com.orbvpn.api.domain.entity.Device;
import com.orbvpn.api.domain.entity.Radacct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface RadAcctRepository extends JpaRepository<Radacct, Integer> {
    long deleteByUsername(String username);

    @Query("SELECT radAcct FROM Radacct radAcct, User user WHERE radAcct.username = user.email AND" +
            " user.id = :userId AND radAcct.acctstoptime IS NOT NULL")
    List<Radacct> findConnectionHistory(Integer userId);

    @Query("SELECT radAcct FROM Radacct radAcct, User user WHERE radAcct.username = user.email AND " +
            "user.id = :userId AND radAcct.acctstoptime IS NULL")
    List<Radacct> findOnlineSessions(Integer userId);

    @Query(value = "select radacct.* FROM radacct WHERE acctsessionid=?1", nativeQuery = true)
    Collection<Radacct> findBySessionid(String acctsessionid);

    @Query(value = "SELECT radacct.connectinfo_start as deviceInfo, radacct.acctstarttime as lastConnectionStartTime, " +
            "radacct.acctstoptime as lastConnectionStopTime, radacct.acctsessionid as lastSessionId, " +
            "srv.id as lastConnectedServerId, srv.country as lastConnectedServerCountry, srv.city as lastConnectedServerCity " +
            "from radacct, server srv " +
            "where radacct.nasipaddress = srv.private_ip AND " +
            "        radacct.radacctid in " +
            "        ((select max(radacct.radacctid) " +
            "          FROM radacct, user " +
            "          WHERE radacct.username = user.email AND user.id = :userId " +
            "          GROUP BY radacct.connectinfo_start) " +
            "         union " +
            "         (select radacct.radacctid " +
            "          FROM radacct, user " +
            "          WHERE radacct.username = user.email AND user.id = :userId AND " +
            "              radacct.acctstoptime IS NULL))", nativeQuery = true )
    List<Device> getDevices(Integer userId);

    @Query(value = "SELECT radacct.connectinfo_start as deviceInfo, radacct.acctstarttime as lastConnectionStartTime, " +
            "radacct.acctstoptime as lastConnectionStopTime, radacct.acctsessionid as lastSessionId, " +
            "srv.id as lastConnectedServerId, srv.country as lastConnectedServerCountry, srv.city as lastConnectedServerCity " +
            "from radacct, server srv " +
            "where radacct.nasipaddress = srv.private_ip AND " +
            "        radacct.radacctid in " +
            "        ((select max(radacct.radacctid) " +
            "          FROM radacct, user " +
            "          WHERE radacct.username = :email " +
            "          GROUP BY radacct.connectinfo_start) " +
            "         union " +
            "         (select radacct.radacctid " +
            "          FROM radacct, user " +
            "          WHERE radacct.username = :email AND " +
            "              radacct.acctstoptime IS NULL))", nativeQuery = true )
    List<Device> getDevicesByEmail(String email);

    @Query("SELECT radAcct FROM Radacct radAcct, User user WHERE radAcct.username = user.email AND  user.id = :userId AND " +
            "radAcct.acctstoptime IS NULL AND radAcct.connectinfo_start LIKE  '%'|| (:deviceId) ||'%' ")
    Radacct getOnlineSessionByUseridAndDeviceId(Integer userId, String deviceId);

    @Query("SELECT radAcct FROM Radacct radAcct, User user WHERE radAcct.username = :username AND " +
            "radAcct.acctstoptime IS NULL AND radAcct.connectinfo_start LIKE  '%'|| (:deviceId) ||'%'")
    Radacct getOnlineSessionByUsernameAndDeviceId(String username, String deviceId);
}
