package com.orbvpn.api.domain.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@Entity
public class ResellerScoreLimit {

    @Id
    private int id;

    @Column
    private String scoreDefinition;

    @Column(unique = true)
    private String symbol;

    @Column
    private Integer maximumLimit;

}
