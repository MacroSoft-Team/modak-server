package com.macrosoft.modakserver.domain.member.entity;

import com.macrosoft.modakserver.global.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@Entity
@Table(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String clientId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    @Setter
    @Column(nullable = false)
    private String nickname;

    private String deviceToken;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PermissionRole permissionRole;

//    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
//    private List<Log> logs; // 1:N 관계

    public void deactivate() {
        this.clientId = "";
        this.nickname = "알 수 없음";
        this.deviceToken = null;
    }
}