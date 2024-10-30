package com.macrosoft.modakserver.domain.campfire.entity;

import com.macrosoft.modakserver.global.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@Entity
@Table(name = "campfire")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Campfire extends BaseEntity {
    @Id
    private int id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "campfire", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<MemberCampfire> memberCampfires;
}
