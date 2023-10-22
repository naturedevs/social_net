package com.orbvpn.api.domain.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class User implements UserDetails {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(nullable = false)
  private String username;

  @Column(nullable = false, unique = true)
  @Email
  private String email;

  @Column
  private String password;

  @Column
  private String oauthId;

  @ManyToOne
  private Role role;

  @Column(nullable = false)
  private String radAccess = "not-a-regular-user";

  @Column
  private String radAccessClear;

  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  private UserProfile profile;

  @ManyToOne
  private Reseller reseller;

  @Column
  @CreatedDate
  private LocalDateTime createdAt;

  @Column
  @LastModifiedDate
  private LocalDateTime updatedAt;

  @Column
  private boolean enabled = true;

  @Column(columnDefinition="BOOLEAN DEFAULT false")
  private boolean autoRenew = false;

  private transient UserSubscription subscription;

  @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
  private List<PasswordReset> passwordResetList;

  @OneToMany(mappedBy = "creator", cascade = CascadeType.REMOVE)
  private List<Ticket> ticketList;

  @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
  private List<UserSubscription> userSubscriptionList;

  @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
  private List<Payment> paymentList;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.singleton(role.getName());
  }

  @Override
  public boolean isAccountNonExpired() {
    return enabled;
  }

  @Override
  public boolean isAccountNonLocked() {
    return enabled;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return enabled;
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }

  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  private ReferralCode referralCode;
}
