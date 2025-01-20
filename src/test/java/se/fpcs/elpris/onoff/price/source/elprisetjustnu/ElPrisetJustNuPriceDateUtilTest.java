package se.fpcs.elpris.onoff.price.source.elprisetjustnu;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static se.fpcs.elpris.onoff.price.source.elprisetjustnu.ElPrisetJustNuPriceDateUtil.toHour;
import static se.fpcs.elpris.onoff.price.source.elprisetjustnu.ElPrisetJustNuPriceDateUtil.toYYYYMMDD;

class ElPrisetJustNuPriceDateUtilTest {

    @ParameterizedTest
    @CsvSource({
            "2025-01-20T00:00:00+01:00, 00",
            "2025-01-20T12:00:00+01:00, 12",
    })
    void testToHour(String strDate, String expectedHour) {

        assertEquals(
                expectedHour,
                toHour(strDate));

    }

    @ParameterizedTest
    @CsvSource({
            "2025-01-20T00:00:00+01:00, 00",
            "2025-01-20T12:00:00+01:00, 12",
    })
    void testToYYYYMMDD(String strDate, String expectedDay) {

        assertEquals(
                "2025-01-20",
                toYYYYMMDD("2025-01-20T00:00:00+01:00"));

    }

}