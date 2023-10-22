package com.orbvpn.api.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class ConnectionHistoryView {
    private String sessionId;
    private Integer id;
    private String nasIpAddress;
    private String nasPortId;
    private String nasPortType;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ssz")
    private LocalDateTime startTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ssz")
    private LocalDateTime stopTime;
    private String connectInfoStart;
    private String callingStationId;
    private String terminateCause;
    private String framedIpAddress;
}
