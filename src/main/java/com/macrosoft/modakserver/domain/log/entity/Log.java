package com.macrosoft.modakserver.domain.log.entity;

import com.macrosoft.modakserver.domain.campfire.entity.Campfire;
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
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private LocalDateTime start_at;

    @Column(nullable = false)
    private LocalDateTime end_at;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campfire_id")
    private Campfire campfire;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "location_id")
    private Location location;

    @OneToMany(mappedBy = "log", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private List<LogImage> logImages = new ArrayList<>();

    public void addLogImage(LogImage logImage) {
        logImage.setLog(this);
        this.logImages.add(logImage);
    }

    public void removeLogImage(LogImage logImage) {
        logImage.setLog(null);
        this.logImages.remove(logImage);
    }
}
