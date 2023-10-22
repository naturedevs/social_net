package com.orbvpn.api.domain.dto;

import com.orbvpn.api.domain.entity.User;
import com.orbvpn.api.domain.entity.UserProfile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class BulkUserCreate {

    private List<User> users;
    private List<UserProfile> profiles;
    private List<BulkSubscription> subscriptions;
}
