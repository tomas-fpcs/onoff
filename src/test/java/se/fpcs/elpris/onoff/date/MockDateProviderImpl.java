package se.fpcs.elpris.onoff.date;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@Profile("test")
class MockDateProviderImpl implements DateProvider {

    private final Date date;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

    public MockDateProviderImpl() throws ParseException {
        this.date = dateFormat.parse("2024-05-30T16:00:00+02:00");
    }

    @Override
    public Date now() {
        return date;
    }
}