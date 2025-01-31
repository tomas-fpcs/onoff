package se.fpcs.elpris.onoff.price.source.elprisetjustnu;

import static java.util.Objects.requireNonNull;
import static se.fpcs.elpris.onoff.Constants.defaultTimeZone;

import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.Date;

public class EPJN_DateUtil {

  private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
      "yyyy-MM-dd'T'HH:mm:ssXXX");

  public static String toHour(String strDate) {
    requireNonNull(strDate, "strDate must not be null");
    return strDate.substring(11, 13);
  }

  public static long toTimeMs(String strDate) {
    requireNonNull(strDate, "strDate must not be null");
    return stringToDate(strDate).getTime();
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
    return date.toInstant()
        .atZone(defaultTimeZone.toZoneId())
        .format(formatter);
  }

  protected static Date stringToDate(String strDate) {
    Date date = new Date();
    date.setTime(1000L * formatter.parse(strDate).getLong(ChronoField.INSTANT_SECONDS));
    return date;
  }

}
