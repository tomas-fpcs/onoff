package se.fpcs.elpris.onoff.date;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Profile("!test")
@Log4j2
public class DateProviderImpl implements DateProvider {

    public static final long MAX_DIFF_MS = 1000L;

    public DateProviderImpl(NTPClient ntpClient) {

        Date systemDate = now();
        Date ntpDate = ntpClient.getDate().get();

        long diff = Math.abs(
                systemDate.getTime() -
                        ntpDate.getTime());

        if (diff > MAX_DIFF_MS) {
            log.error("The system time \"{}\" is not in sync with NTP time \"{}\", diff: {}",
                    systemDate,
                    ntpDate,
                    diff);
        }

    }

    @Override
    public Date now() {
        return new Date();
    }

}
