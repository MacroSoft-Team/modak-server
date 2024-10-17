package com.macrosoft.modakserver.domain.log.entity;

import com.macrosoft.modakserver.global.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Location extends BaseEntity {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double minLatitude;

    @Column(nullable = false)
    private Double maxLatitude;

    @Column(nullable = false)
    private Double minLongitude;

    @Column(nullable = false)
    private Double maxLongitude;

    @Column(nullable = false)
    private String address;
}
