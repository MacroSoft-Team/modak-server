package com.macrosoft.modakserver.domain.log.entity;

import com.macrosoft.modakserver.domain.campfire.entity.Campfire;
import com.macrosoft.modakserver.domain.image.entity.LogImage;
import com.macrosoft.modakserver.domain.log.dto.LogRequest.ImageInfo;
import com.macrosoft.modakserver.domain.log.dto.LogRequest.UploadLog;
import com.macrosoft.modakserver.domain.log.dto.LogResponse.LogMetadata;
import com.macrosoft.modakserver.domain.member.entity.Member;
import com.macrosoft.modakserver.global.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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
@Table(name = "log")
public class Log extends BaseEntity {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime startAt;

    @Column(nullable = false)
    private LocalDateTime endAt;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campfire_id")
    private Campfire campfire;

    @Setter
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "location_id")
    private Location location;

    @OneToMany(mappedBy = "log", fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @Builder.Default
    @OrderBy("takenAt ASC")
    private List<LogImage> logImages = new ArrayList<>();

    public void addLogImage(LogImage logImage) {
        logImage.setLog(this);
        this.logImages.add(logImage);
    }

    public void removeLogImage(LogImage logImage) {
        logImage.setLog(null);
        this.logImages.remove(logImage);
    }

    public static Log of(Campfire campfire, Member member, UploadLog uploadLog) {
        LogMetadata logMetadata = uploadLog.logMetadata();
        Location location = Location.builder()
                .minLatitude(logMetadata.minLatitude())
                .maxLatitude(logMetadata.maxLatitude())
                .minLongitude(logMetadata.minLongitude())
                .maxLongitude(logMetadata.maxLongitude())
                .address(logMetadata.address())
                .build();

        Log log = Log.builder()
                .startAt(logMetadata.startAt())
                .endAt(logMetadata.endAt())
                .campfire(campfire)
                .location(location)
                .build();

        for (ImageInfo imageInfo : uploadLog.imageInfos()) {
            LogImage logImage = LogImage.builder()
                    .name(imageInfo.imageName())
                    .latitude(imageInfo.latitude())
                    .longitude(imageInfo.longitude())
                    .takenAt(imageInfo.takenAt())
                    .member(member)
                    .log(log)
                    .build();
            log.addLogImage(logImage);
        }

        return log;
    }

    public boolean isSameEvent(Log other) {
        return isTimeOverlap(other) && isLocationOverlap(other);
    }

    private boolean isTimeOverlap(Log other) {
        return this.startAt.isBefore(other.endAt) &&
                this.endAt.isAfter(other.startAt);
    }

    private boolean isLocationOverlap(Log other) {
        Location existLocation = this.location;
        Location uploadLocation = other.location;
        return existLocation.isOverlap(uploadLocation);
    }

    public void update(Location newLocation, LocalDateTime newStartAt, LocalDateTime newEndAt,
                       List<LogImage> newLogImages) {
        this.startAt = newStartAt;
        this.endAt = newEndAt;
        this.location.update(newLocation);

        Set<String> existingImageNames = this.logImages.stream()
                .map(LogImage::getName)
                .collect(Collectors.toSet());

        for (LogImage newLogImage : newLogImages) {
            if (!existingImageNames.contains(newLogImage.getName())) {
                addLogImage(newLogImage);
            }
        }
    }
}
