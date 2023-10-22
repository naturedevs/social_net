package com.orbvpn.api.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class OnlineSessionView {
    private Integer id;
    private String sessionId;
    private String nasIpAddress;
    private String nasPortId;
    private String nasPortType;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ssz")
    private LocalDateTime startTime;
    private String connectInfoStart;
    private String callingStationId;
    private String framedIpAddress;
}
