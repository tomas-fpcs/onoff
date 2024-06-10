package se.fpcs.elpris.onoff.date;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.net.ntp.NTPUDPClient;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Optional;

import static java.lang.String.format;

@Component
@Log4j2
public class NTPClient {

    private static final String ntpServerAddress = "se.pool.ntp.org"; //TODO configurable?

    private final NTPUDPClient timeClient;
    private final InetAddress inetAddress;

    public NTPClient() {

        this.timeClient = new NTPUDPClient();
        try {
            this.inetAddress = InetAddress.getByName(ntpServerAddress);
        } catch (UnknownHostException e) {
            log.error("Unknown time server: {}", ntpServerAddress);
            throw new RuntimeException(e);
        }
    }

    public Optional<Date> getDate() {

        try {
            final long begin = System.currentTimeMillis();
            Optional<Date> optionalDate = Optional.of(new Date(timeClient.getTime(inetAddress)
                    .getMessage()
                    .getTransmitTimeStamp()
                    .getTime()));
            log.info("Got time from NTP, took {} ms", (System.currentTimeMillis() - begin));
            return optionalDate;
        } catch (IOException e) {
            log.error("Could not get time", e);
            return Optional.empty();
        }

    }

}
