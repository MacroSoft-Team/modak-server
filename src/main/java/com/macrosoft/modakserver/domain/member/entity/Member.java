package com.macrosoft.modakserver.domain.member.entity;

import com.macrosoft.modakserver.domain.campfire.entity.MemberCampfire;
import com.macrosoft.modakserver.global.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Setter
    private String deviceToken;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PermissionRole permissionRole;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "avatar_id")
    @Builder.Default
    private Avatar avatar = new Avatar();

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private List<MemberCampfire> memberCampfires = new ArrayList<>();

    public void deactivate() {
        this.clientId = "";
        this.nickname = "알 수 없음";
        this.deviceToken = null;
        this.avatar = null;
    }

    public void addMemberCampfire(MemberCampfire memberCampfire) {
        memberCampfire.setMember(this);
        this.memberCampfires.add(memberCampfire);
    }

    public void removeMemberCampfire(MemberCampfire memberCampfire) {
        memberCampfire.setMember(null);
        this.memberCampfires.remove(memberCampfire);
    }
}