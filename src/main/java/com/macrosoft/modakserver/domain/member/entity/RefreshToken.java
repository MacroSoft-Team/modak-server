package com.macrosoft.modakserver.domain.member.entity;

import jakarta.persistence.*;
import java.util.Date;
import lombok.*;

@Entity
@Getter
@Builder
@Table(name = "refresh_token")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String clientId;

    @Setter
    @Column(length = 500)
    private String token;

    @Setter
    private Date expirationDate;
}
