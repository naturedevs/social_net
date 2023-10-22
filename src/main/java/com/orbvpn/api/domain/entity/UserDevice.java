package com.orbvpn.api.domain.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(indexes = {
        @Index(columnList = "user_id"),
        @Index(columnList = "deviceId")
})
public class UserDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_user_device"))
    private User user;

    @Column
    private String os;

    @Column
    private String deviceId;

    @Column
    private LocalDateTime loginDate;

    @Column
    private LocalDateTime logoutDate;

    @Column
    private String appVersion;

    @Column
    private String deviceModel;

    @Column
    private String deviceName;

    @Column
    private String fcmToken;

    @Column
    private Boolean isActive;

    @Column
    private Boolean isBlocked = false;
}