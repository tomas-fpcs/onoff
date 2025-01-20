package se.fpcs.elpris.onoff.date;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * This service logs an error if the system time is not in sync with time retrieved using NTP.
 */
@Service
@Profile("!test")
@Log4j2
public class DateCheckerService {

    public static final long MAX_DIFF_MS = 10000L;

    private final NTPClient ntpClient;

    @Autowired
    public DateCheckerService(NTPClient ntpClient) {
        this.ntpClient = ntpClient;
    }

    @Scheduled(fixedRate = 24 * 60 * 60 * 1000) // 24 hours in milliseconds
    public void checkDate() {

        final Date ntpDate = ntpClient.getDate().get();

        final Date systemDate = new Date();
        final long diff = Math.abs(
                systemDate.getTime() -
                        ntpDate.getTime());

        if (diff > MAX_DIFF_MS) {
            log.error("The system time \"{}\" is not in sync with NTP time \"{}\", diff: {} ms",
                    systemDate,
                    ntpDate,
                    diff);
        }

    }

}
