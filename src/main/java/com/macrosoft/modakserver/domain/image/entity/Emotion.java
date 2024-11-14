package com.macrosoft.modakserver.domain.image.entity;

import com.macrosoft.modakserver.domain.member.entity.Member;
import com.macrosoft.modakserver.global.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
@Table(name = "emote")
public class Emotion extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String emotion;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "log_image_id")
    private LogImage logImage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public static Emotion of(String emotion, LogImage logImage, Member member) {
        return Emotion.builder()
                .emotion(emotion)
                .logImage(logImage)
                .member(member)
                .build();
    }

    public void updateEmotion(String emotion) {
        this.emotion = emotion;
    }
}
