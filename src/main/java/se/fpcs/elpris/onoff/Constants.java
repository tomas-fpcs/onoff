package se.fpcs.elpris.onoff;

import java.util.TimeZone;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {

  public static final String ONOFF_V1 = "/api/v1";

  public static final TimeZone defaultTimeZone = TimeZone.getTimeZone("Europe/Stockholm");

}
