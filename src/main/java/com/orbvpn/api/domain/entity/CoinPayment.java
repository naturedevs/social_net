package com.orbvpn.api.domain.entity;

import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;


@Getter
@Setter
@Entity
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class CoinPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "none"))
    private User user;

    @OneToOne
    @JoinColumn(name = "payment_id", foreignKey = @ForeignKey(name = "none"))
    private Payment payment;

    @Column
    private String txnId;

    @Column
    private Boolean status;

    @Column
    private String coin;

    @Column
    private String coinAmount;

    @Column
    private String address;

    @Column
    private String confirms_needed;

    @Column
    private Integer timeout;

    @Column
    private String checkout_url;

    @Column
    private String status_url;

    @Column
    private String qrcode_url;

}
