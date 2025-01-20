package se.fpcs.elpris.onoff.price.source.elprisetjustnu;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static java.util.Objects.requireNonNull;
import static se.fpcs.elpris.onoff.Constants.defaultTimeZone;

public class ElPrisetJustNuPriceDateUtil {

    public static String toHour(String strDate) {
        requireNonNull(strDate, "strDate must not be null");
        return strDate.substring(11, 13);
    }

    public static String toYYYYMMDD(String strDate) {
        requireNonNull(strDate, "strDate must not be null");
        return strDate.substring(0, 10);
    }

    public static String toHour(Date date) {
        return toHour(dateToString(date));
    }

    public static String toYYYYMMDD(Date date) {
        return toYYYYMMDD(dateToString(date));
    }

    protected static String dateToString(Date date) {
        ZonedDateTime zonedDateTime = date.toInstant().atZone(defaultTimeZone.toZoneId());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
        String strDate = zonedDateTime.format(formatter);
        return strDate;
    }

}
