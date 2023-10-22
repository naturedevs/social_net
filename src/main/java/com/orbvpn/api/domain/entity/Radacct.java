package com.orbvpn.api.domain.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class Radacct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int radacctid;

    @Column
    private String acctsessionid;

    @Column(unique = true)
    private String acctuniqueid;

    @Column
    private String username;

    @Column
    private String realm;

    @Column
    private String nasipaddress;

    @Column
    private String nasportid;

    @Column
    private String nasporttype;

    @Column
    private LocalDateTime acctstarttime;

    @Column
    private LocalDateTime acctupdatetime;

    @Column
    private LocalDateTime acctstoptime;

    @Column
    private Integer acctinterval;

    @Column
    private Integer acctsessiontime;

    @Column
    private String acctauthentic;

    @Column
    private String connectinfo_start;

    @Column
    private String connectinfo_stop;

    @Column
    private Long acctinputoctets;

    @Column
    private Long acctoutputoctets;

    @Column
    private String calledstationid;

    @Column
    private String callingstationid;

    @Column
    private String acctterminatecause;

    @Column
    private String servicetype;

    @Column
    private String framedprotocol;

    @Column
    private String framedipaddress;

    @Column
    private String framedipv6address;

    @Column
    private String framedipv6prefix;

    @Column
    private String framedinterfaceid;

    @Column
    private String delegatedipv6prefix;

    public Boolean isOnlineSession() {
        return acctstoptime == null;
    }
}
