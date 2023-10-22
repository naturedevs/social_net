package com.orbvpn.api.domain.entity;

import com.orbvpn.api.domain.enums.GatewayName;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@ToString
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private int id;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column
    private String clientCompanyName;

    @Column
    private String companyRegistrationNumber;

    @Column
    private String address;

    @Column
    private String taxId;

    @Column(nullable = false)
    private String email;

    @Column
    private String phoneNumber;

    @OneToOne
    @JoinColumn(name = "payment_id", foreignKey = @ForeignKey(name = "invoicePaymentForeignKey"))
    private Payment payment;

    @Column
    private int groupId;

    @Column
    @DecimalMin(value = "0.0")
    private BigDecimal amountForGroup;

    @Column
    private int multiLogin;

    @Column
    @DecimalMin(value = "0.0")
    private BigDecimal amountForMultiLogin;

    @Column
    @DecimalMin(value = "0.0")
    private BigDecimal totalAmount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private GatewayName paymentMethod;

    @Column
    private LocalDateTime paymentDate;

    @Column
    @CreatedDate
    private LocalDateTime invoiceDate;

    @Column
    @LastModifiedDate
    private LocalDateTime updateDate;

    public String getCustomerName() {
        return this.firstName + " " + this.lastName;
    }
}
