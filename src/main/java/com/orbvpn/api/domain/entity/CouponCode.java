package com.orbvpn.api.domain.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class CouponCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String couponCode;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate expiryDate;

    @Column(nullable = false)
    private int discountRate;

    @Column(nullable = false)
    private int quantity;

    @Column
    @CreatedDate
    private LocalDateTime createdAt;

    public boolean isCodeValid() {
        LocalDate today = LocalDate.now();
        return !expiryDate.isAfter(today) || quantity <= 0;
    }

    public void decreaseQuantity() {
        this.quantity -= 1;
    }
}
