package com.orbvpn.api.domain.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class TokenCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String tokenCode;

    @ManyToOne(optional = false)
    private User user;

    @Column(nullable = false)
    private int discountRate;

    @Column
    private boolean isActive = true;

    @Column
    private LocalDateTime usedTimestamp;

    @Column
    @CreatedDate
    private LocalDateTime createdAt = LocalDateTime.now();

}
