package se.fpcs.elpris.onoff.date;

import java.net.UnknownHostException;

public class UnknownNTPHostException extends RuntimeException {
    public UnknownNTPHostException(UnknownHostException e) {
        super("Unknown NTP host: " + e.getMessage());
    }
}
