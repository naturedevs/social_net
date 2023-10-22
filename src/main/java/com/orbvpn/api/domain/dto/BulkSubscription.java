package com.orbvpn.api.domain.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BulkSubscription {
    private Integer groupId;
    private Integer duration;
    private Integer multiLoginCount;
}
