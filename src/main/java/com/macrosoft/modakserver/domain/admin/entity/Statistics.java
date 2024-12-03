package com.macrosoft.modakserver.domain.admin.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@Entity
@Table(name = "statistics")
@NoArgsConstructor
@AllArgsConstructor
public class Statistics {
    @Id
    private LocalDate date;
    private Long emotionCount;
    private Long campfireCount;
    private Long logCount;
    private Long imageCount;
    private Long memberCount;
    private Long activeMemberCount;
}
