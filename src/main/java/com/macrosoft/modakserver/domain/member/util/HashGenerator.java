package com.macrosoft.modakserver.domain.member.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashGenerator {
    public static String encrypt(String input, String salt) {
        try {
            // SHA-256 해시 알고리즘 인스턴스 생성
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            // 입력 문자열을 바이트 배열로 변환하고 해시 생성
            byte[] hashBytes = digest.digest(input.getBytes());
            // 바이트 배열을 16진수 문자열로 변환
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0'); // 1자리 수의 경우 앞에 0 추가
                }
                hexString.append(hex);
            }
            return hexString.toString(); // 해시 결과 반환
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e); // 예외 처리
        }
    }
}
