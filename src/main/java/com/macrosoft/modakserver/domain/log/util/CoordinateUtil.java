package com.macrosoft.modakserver.domain.log.util;

public class CoordinateUtil {

    private static final int DEFAULT_SCALE = 6;

    /**
     * 주어진 값을 소수점 이하 특정 자리수로 자릅니다.
     *
     * @param value 대상 값
     * @param scale 소수점 이하 자리수
     * @return 자른 값
     */
    public static double truncate(double value, int scale) {
        double factor = Math.pow(10, scale);
        return Math.round(value * factor) / factor;
    }

    /**
     * 주어진 값을 소수점 이하 6자리로 자릅니다.
     *
     * @param value 대상 값
     * @return 자른 값
     */
    public static double truncate(double value) {
        return truncate(value, DEFAULT_SCALE);
    }
}