package com.macrosoft.modakserver.domain.log.entity;

import static com.macrosoft.modakserver.domain.log.util.CoordinateUtil.truncate;

import com.macrosoft.modakserver.global.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Location extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double minLatitude;

    private Double maxLatitude;

    private Double minLongitude;

    private Double maxLongitude;

    @Setter
    @Column(nullable = false)
    private String address;

    public static Location of(Double minLatitude, Double maxLatitude, Double minLongitude, Double maxLongitude,
                              String address) {
        double truncatedMinLatitude = truncate(minLatitude);
        double truncatedMaxLatitude = truncate(maxLatitude);
        double truncatedMinLongitude = truncate(minLongitude);
        double truncatedMaxLongitude = truncate(maxLongitude);

        return Location.builder()
                .minLatitude(truncatedMinLatitude)
                .maxLatitude(truncatedMaxLatitude)
                .minLongitude(truncatedMinLongitude)
                .maxLongitude(truncatedMaxLongitude)
                .address(address)
                .build();
    }

    public boolean isOverlap(Location other) {
        return this.minLatitude < other.maxLatitude &&
                this.maxLatitude > other.minLatitude &&
                this.minLongitude < other.maxLongitude &&
                this.maxLongitude > other.minLongitude;
    }

    public Location merge(Location other) {
        return Location.builder()
                .minLatitude(Math.min(this.minLatitude, other.minLatitude))
                .maxLatitude(Math.max(this.maxLatitude, other.maxLatitude))
                .minLongitude(Math.min(this.minLongitude, other.minLongitude))
                .maxLongitude(Math.max(this.maxLongitude, other.maxLongitude))
                .address(this.address)
                .build();
    }

    public void update(Location other) {
        this.minLatitude = other.minLatitude;
        this.maxLatitude = other.maxLatitude;
        this.minLongitude = other.minLongitude;
        this.maxLongitude = other.maxLongitude;
        this.address = other.address;
    }
}
