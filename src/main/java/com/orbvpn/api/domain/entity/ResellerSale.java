package com.orbvpn.api.domain.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ResellerSale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    private Reseller reseller;

    @ManyToOne
    private Group group;

    @ManyToOne
    private User user;

    @Column
    private BigDecimal price;

    @Column
    @CreatedDate
    private LocalDateTime createdAt;
}
