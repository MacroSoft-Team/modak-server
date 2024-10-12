package com.macrosoft.modakserver.member.model;

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

    private String nickname;

    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    private String socialId;

//    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
//    private List<Log> logs; // 1:N 관계
}