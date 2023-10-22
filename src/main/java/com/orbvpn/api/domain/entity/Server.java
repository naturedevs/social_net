package com.orbvpn.api.domain.entity;

import com.orbvpn.api.domain.enums.ServerType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class Server {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column
  private String hostName;

  @Column(unique = true)
  private String publicIp;

  @Column
  @Enumerated(EnumType.STRING)
  private ServerType type;

  @Column
  private String privateIp;

  @Column
  private String city;

  @Column
  private String country;

  @Column
  private String continent;

  @Column
  private String secret;

  @Column
  private Integer ports;

  @Column
  private String sshUsername;

  @Column
  private String sshKey;

  @Column
  private String sshPrivateKey;

  @Column
  private String killCommand;

  @Column
  private String rootCommand;

  @Column
  private String description;

  @Column
  private int cryptoFriendly;

  @Column(length = 1000)
  private String hero;

  @Column(length = 1000)
  private String spot;

  @Column(length = 1000)
  private String zeus;

  @Column
  private String bridgeIp;

  @Column
  private String bridgeCountry;

  @Column
  private int hide;

  @Column
  @CreatedDate
  private LocalDateTime createdAt;

  @Column
  @LastModifiedDate
  private LocalDateTime updatedAt;
}
