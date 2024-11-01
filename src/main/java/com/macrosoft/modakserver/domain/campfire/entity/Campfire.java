package com.macrosoft.modakserver.domain.campfire.entity;

import com.macrosoft.modakserver.global.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@Entity
@Table(name = "campfire", indexes = @Index(columnList = "pin"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Campfire extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private int pin;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "campfire", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private List<MemberCampfire> memberCampfires = new ArrayList<>();

    public void addMemberCampfire(MemberCampfire memberCampfire) {
        memberCampfire.setCampfire(this);
        this.memberCampfires.add(memberCampfire);
    }
}
