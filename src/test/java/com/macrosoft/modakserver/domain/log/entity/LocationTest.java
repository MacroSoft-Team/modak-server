package com.macrosoft.modakserver.domain.log.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class LocationTest {

    @Test
    void 모든_위치_좌표는_소수점_6자리까지만_가능하다() {
        // given
        double minLatitude = 0;
        double maxLatitude = 37.123456789;
        double minLongitude = 123.54876666666667;
        double maxLongitude = 127.1234;

        // when
        Location location = Location.of(minLatitude, maxLatitude, minLongitude, maxLongitude, "");

        // then
        assertThat(location.getMinLatitude()).isEqualTo(0);
        assertThat(location.getMaxLatitude()).isEqualTo(37.123457);
        assertThat(location.getMinLongitude()).isEqualTo(123.548767);
        assertThat(location.getMaxLongitude()).isEqualTo(127.1234);
    }
}