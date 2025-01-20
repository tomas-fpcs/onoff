package se.fpcs.elpris.onoff;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.TimeZone;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {

    public static final String ONOFF_V1 = "/api/v1";

    public static final TimeZone defaultTimeZone = TimeZone.getTimeZone("Europe/Stockholm");

}
