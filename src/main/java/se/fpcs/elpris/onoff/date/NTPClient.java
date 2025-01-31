package se.fpcs.elpris.onoff.date;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.net.ntp.NTPUDPClient;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class NTPClient {

  private final NTPUDPClient timeClient;
  private final InetAddress inetAddress;

  public NTPClient(NTPProperties ntpProperties) {

    this.timeClient = new NTPUDPClient();
    try {
      this.inetAddress = InetAddress.getByName(ntpProperties.getHostname());
    } catch (UnknownHostException e) {
      log.error("Unknown NTP server: {}", ntpProperties.getHostname());
      throw new UnknownNTPHostException(e);
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
