package se.fpcs.elpris.onoff.date;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    private DateUtil() {
    }

    public static LocalDate getLocalDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return LocalDate.of(
                calendar.get(Calendar.YEAR),
                1 + calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
    }

    public static Integer getHour(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

}
