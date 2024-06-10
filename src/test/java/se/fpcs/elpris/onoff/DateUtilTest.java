package se.fpcs.elpris.onoff;

import org.junit.jupiter.api.Test;
import se.fpcs.elpris.onoff.date.DateUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DateUtilTest {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

    @Test
    void shouldGetLocalDate() throws ParseException {


        assertEquals(
                LocalDate.of(2024, 5, 26),
                DateUtil.getLocalDate(
                        dateFormat.parse("2024-05-26T02:00:00+02:00")));

    }

    @Test
    void shouldGetHour() throws ParseException {

        assertEquals(
                2,
                DateUtil.getHour(
                        dateFormat.parse("2024-05-26T02:00:00+02:00")));

        assertEquals(
                23,
                DateUtil.getHour(
                        dateFormat.parse("2024-05-26T23:00:00+02:00")));

    }
}