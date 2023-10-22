package com.orbvpn.api.reposiitory;

import com.orbvpn.api.domain.entity.Reseller;
import com.orbvpn.api.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByOauthId(String oauthId);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    long countByCreatedAtAfter(LocalDateTime createdAt);

    Page<User> findAllByReseller(Reseller reseller, Pageable pageable);

    @Query("SELECT user from User user where user.id not in (select sub.user.id from UserSubscription sub where sub.expiresAt > :dateTime)")
    Page<User> findAllExpiredUsers(LocalDateTime dateTime, Pageable pageable);

    @Query("SELECT user from User user where user.reseller = :reseller and user.id not in (select sub.user.id from UserSubscription sub where sub.expiresAt > :dateTime)")
    Page<User> findAllResellerExpiredUsers(Reseller reseller, LocalDateTime dateTime, Pageable pageable);

    @Query(
            value = "SELECT count(*) FROM user where role_id=3 and email in (select username from radacct where acctstoptime IS NULL)",
            nativeQuery = true)
    int getTotalActiveUsers();

    @Query(
            value = "SELECT count(*) FROM user where role_id=3 and reseller_id=:resellerId and email in (select username from radacct where acctstoptime IS NULL)",
            nativeQuery = true)
    int getActiveUsersOfReseller(int resellerId);

    @Query(
            value = "SELECT * FROM user_profile user_profile join user on user.id=user_profile.user_id where user.role_id=3 and user.email in (select username from radacct where acctstoptime IS NULL)",
            countQuery = "SELECT count(*) FROM user join user_profile on user.id=user_profile.user_id where role_id=3 and email in (select username from radacct where acctstoptime IS NULL)",
            nativeQuery = true)
    Page<User> findAllActiveUsers(Pageable pageable);

    @Query(
            value = "SELECT * FROM user_profile user_profile join user on user.id=user_profile.user_id where user.role_id=3 and user.email not in (select username from radacct where acctstoptime IS NULL)",
            countQuery = "SELECT count(*) FROM user join user_profile on user.id=user_profile.user_id where role_id=3 and email not in (select username from radacct where acctstoptime IS NULL)",
            nativeQuery = true)
    Page<User> findAllNotActiveUsers(Pageable pageable);

    //     @Query(
//             value = "select u from User u where u.role.id = 3 and ",
//             countQuery = "SELECT count(*) FROM User where role_id=3"
//         )
    Page<User> findByRoleIdAndEmailContaining(int roleId, String email, Pageable pageable);

    Page<User> findByRoleIdAndUsernameContaining(int roleId, String username, Pageable pageable);

    @Query("select u from User u where u.role.id = 3 and :param like '%:query%'")
    Page<User> findByParam(@Param("param") String param, @Param("query") String query, Pageable pageable);

    @Query(
            value = "SELECT * FROM user where role_id=3 and email like %?1",
            countQuery = "SELECT count(*) FROM user where role_id=3 and email like %?1",
            nativeQuery = true)
    Page<User> findAllUsers(String query, Pageable pageable);

    @Query(value = "select distinct(connectinfo_start) from radacct where username = :username", nativeQuery = true)
    List<String> findAllUserDevices(String username);

    @Query(value = "select distinct(connectinfo_start) from radacct where username = :username and acctstoptime is null", nativeQuery = true)
    List<String> findAllActiveUserDevices(String username);

    @Query(value = "select user.* " +
            "FROM user " +
            "         LEFT JOIN radacct radAcct ON radAcct.username = user.email " +
            "         LEFT JOIN user_subscription subs ON user.id = subs.user_id " +
            "         LEFT JOIN group_app g ON subs.group_id = g.id " +
            "         LEFT JOIN service_group sg ON g.service_group_id = sg.id " +
            "         LEFT JOIN role r ON user.role_id = r.id " +
            "         LEFT JOIN server ON radAcct.nasipaddress = server.private_ip " +
            "WHERE radAcct.acctstoptime IS NULL " +
            "  AND (:roleId IS NULL OR r.id = :roleId) " +
            "  AND (:groupId IS NULL OR g.id = :groupId) " +
            "  AND (:serverId IS NULL OR server.id = :serverId) " +
            "  AND (:serviceGroupId IS NULL OR :serviceGroupId = sg.id)",
            countQuery = "SELECT count(user.email) " +
                    "FROM user " +
                    "         LEFT JOIN radacct radAcct ON radAcct.username = user.email " +
                    "         LEFT JOIN user_subscription subs ON user.id = subs.user_id " +
                    "         LEFT JOIN group_app g ON subs.group_id = g.id " +
                    "         LEFT JOIN service_group sg ON g.service_group_id = sg.id " +
                    "         LEFT JOIN role r ON user.role_id = r.id " +
                    "         LEFT JOIN server ON radAcct.nasipaddress = server.private_ip " +
                    "WHERE radAcct.acctstoptime IS NULL " +
                    "  AND (:roleId IS NULL OR r.id = :roleId) " +
                    "  AND (:groupId IS NULL OR g.id = :groupId) " +
                    "  AND (:serverId IS NULL OR server.id = :serverId) " +
                    "  AND (:serviceGroupId IS NULL OR :serviceGroupId = sg.id)",
            nativeQuery = true)
    Page<User> findOnlineUsers(Pageable pageable, Integer serverId, Integer groupId, Integer roleId, Integer serviceGroupId);

    @Modifying
    @Query(value = "update user set reseller_id=:new_id where reseller_id=:old_id", nativeQuery = true)
    int updateResellerId(@Param("old_id") int oldId, @Param("new_id") int newId);

}
