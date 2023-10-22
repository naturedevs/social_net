package com.orbvpn.api.reposiitory;

import com.orbvpn.api.domain.entity.User;
import com.orbvpn.api.domain.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Integer> {

    Optional<UserProfile> findByUser(User user);

    @Query("select userProfile from UserProfile userProfile where " +
            "month(userProfile.birthDate)=month(current_date()) and day(userProfile.birthDate)=day(current_date())")
    List<UserProfile> findUsersBornToday();
}
