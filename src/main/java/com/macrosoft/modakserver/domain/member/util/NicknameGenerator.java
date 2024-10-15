package com.macrosoft.modakserver.domain.member.util;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class NicknameGenerator {

    private static final Random random = new Random();

    private static final List<String> ADJECTIVES = List.of(
            "따스한", "포근한", "은은한", "꿈꾸는", "불꽃같은", "잔잔한", "두근두근", "웃음 가득한",
            "이야기하는", "감성적인", "추억 속의", "바람 속의", "별빛 아래", "달빛에 물든", "기쁨의",
            "가슴 뛰는", "캠프파이어", "모닥불 옆", "노을 지는", "설레는", "아련한", "빛나는", "행복한", "소중한",
            "밝은", "따뜻한", "신비로운"
    );

    private static final List<String> CHARACTERS = List.of(
            "꿈돌이", "마시멜로", "불씨", "밤송이", "모닥이", "희망이", "추억이", "잔불이", "담쟁이",
            "별이", "도란이", "푸른밤", "캠프리", "도깨비", "호롱이", "파이어링", "휘파람", "바람돌이",
            "햇살이", "달맞이"
    );

    /**
     * 랜덤 닉네임 생성 메서드
     * @return 생성된 닉네임
     */
    public static String generateRandomNickname() {
        String adjective = ADJECTIVES.get(random.nextInt(ADJECTIVES.size()));
        String character = CHARACTERS.get(random.nextInt(CHARACTERS.size()));
        return adjective + character;
    }
}