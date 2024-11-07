package com.macrosoft.modakserver.domain.campfire.entity;

import com.macrosoft.modakserver.domain.image.entity.LogImage;
import com.macrosoft.modakserver.domain.log.entity.Log;
import com.macrosoft.modakserver.global.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
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
@Table(name = "campfire", indexes = @Index(columnList = "pin"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Campfire extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private int pin;

    @Setter
    @Column(nullable = false)
    private String name;

    @Setter
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "today_image_id")
    private LogImage todayImage;

    @OneToMany(mappedBy = "campfire", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private List<Log> logs = new ArrayList<>();

    @OneToMany(mappedBy = "campfire", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private List<MemberCampfire> memberCampfires = new ArrayList<>();

    public void addLog(Log log) {
        log.setCampfire(this);
        this.logs.add(log);
    }

    public void removeLog(Log log) {
        log.setCampfire(null);
        this.logs.remove(log);
    }

    public void addMemberCampfire(MemberCampfire memberCampfire) {
        memberCampfire.setCampfire(this);
        this.memberCampfires.add(memberCampfire);
    }

    public void removeMemberCampfire(MemberCampfire memberCampfire) {
        memberCampfire.setCampfire(null);
        this.memberCampfires.remove(memberCampfire);
    }
}
