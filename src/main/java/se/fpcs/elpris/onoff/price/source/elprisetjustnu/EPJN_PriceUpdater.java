package se.fpcs.elpris.onoff.price.source.elprisetjustnu;

import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import se.fpcs.elpris.onoff.Constants;
import se.fpcs.elpris.onoff.db.DatabaseOperationException;
import se.fpcs.elpris.onoff.price.PriceForHour;
import se.fpcs.elpris.onoff.price.PriceService;
import se.fpcs.elpris.onoff.price.PriceSource;
import se.fpcs.elpris.onoff.price.PriceUpdaterStatus;
import se.fpcs.elpris.onoff.price.PriceZone;
import se.fpcs.elpris.onoff.price.source.elprisetjustnu.model.EPJN_Price;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static se.fpcs.elpris.onoff.price.source.elprisetjustnu.EPJN_DateUtil.toHour;
import static se.fpcs.elpris.onoff.price.source.elprisetjustnu.EPJN_DateUtil.toTimeMs;
import static se.fpcs.elpris.onoff.price.source.elprisetjustnu.EPJN_DateUtil.toYYYYMMDD;

@Service
@Log4j2
@Profile("!test")
public class EPJN_PriceUpdater {

    private EPJN_Client client;
    private final PriceService priceService;
    private final PriceUpdaterStatus priceUpdaterStatus;

    public EPJN_PriceUpdater(
            EPJN_Client client,
            PriceService priceService,
            PriceUpdaterStatus priceUpdaterStatus) {
        this.client = client;
        this.priceService = priceService;
        this.priceUpdaterStatus = priceUpdaterStatus;
    }

    @PostConstruct
    @Scheduled(cron = "0 0 * * * *") // every hour
    public void refreshPrices() {

        Arrays.stream(PriceZone.values())
                .forEach(this::getContent);
        this.priceUpdaterStatus.setReady(PriceSource.ELPRISETJUSTNU);
        log.info("Prices updated from source: {}", PriceSource.ELPRISETJUSTNU);

    }

    private void getContent(final PriceZone priceZone) {

        try {
            List<Calendar> days = new ArrayList<>();

            Calendar today = Calendar.getInstance();
            today.setTime(new Date());
            days.add(today);

            if (today.get(Calendar.HOUR_OF_DAY) > 22) {
                // I am not sure of the exact time when tomorrow prices are available, but start trying after 2200
                Calendar tomorrow = Calendar.getInstance();
                tomorrow.setTime(today.getTime());
                tomorrow.add(Calendar.DATE, 1);
                days.add(tomorrow);
            }

            days.stream()
                    .forEach(day -> {
                        Optional<EPJN_Price[]> optionalEPJNs = getPrices(priceZone, day);
                        if (optionalEPJNs.isPresent()) {
                            Arrays.stream(optionalEPJNs.get())
                                    .forEach(EPJNPrice -> {
                                        Optional<PriceForHour> optionalPrice = toPrice(priceZone, EPJNPrice);
                                        if (optionalPrice.isPresent()) {
                                            save(optionalPrice.get());
                                        }
                                    });
                        }
                    });
            if (log.isTraceEnabled()) {
                log.trace("Prices saved");
            }
        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                // this is normal early in the day as prices are not yet set for the next day
                if (log.isTraceEnabled()) {
                    log.trace("Not found: {}", e.getMessage());
                }
            } else {
                log.error("HTTP status: {}: {}", e.getStatusCode(), e.getMessage(), e);
            }
        } catch (Exception e) {
            log.error("Error calling {}: {}", PriceSource.ELPRISETJUSTNU.name(), e.getMessage(), e);
        }

    }

    protected Optional<EPJN_Price[]> getPrices(
            PriceZone priceZone,
            Calendar calendar) {

        final String year = String.valueOf(calendar.get(Calendar.YEAR));
        final String month = String.format("%02d", 1 + calendar.get(Calendar.MONTH));
        final String day = String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH));

        try {
            log.info("Retrieving prices for {}-{}-{}, PriceZone: {}",
                    year, month, day, priceZone.name());
            return Optional.of(
                    client.getPrices(
                            year,
                            month,
                            day,
                            priceZone.name()));
        } catch (Exception e) {
            log.error("Failed retrieving prices for {}-{}-{}, PriceZone: {}",
                    year, month, day, priceZone.name());
            return Optional.empty();
        }

    }

    protected Optional<PriceForHour> toPrice(PriceZone priceZone, EPJN_Price EPJNPrice) {

        try {
            return Optional.of(
                    PriceForHour.builder()
                            .priceSource(PriceSource.ELPRISETJUSTNU)
                            .priceZone(priceZone)
                            .sekPerKWh(EPJNPrice.getSekPerKWh())
                            .eurPerKWh(EPJNPrice.getEurPerKWh())
                            .exchangeRate(EPJNPrice.getExr())
                            .priceTimeMs(toTimeMs(EPJNPrice.getTimeStart()))
                            .priceDay(toYYYYMMDD(EPJNPrice.getTimeStart()))
                            .priceHour(toHour(EPJNPrice.getTimeStart()))
                            .priceTimeZone(Constants.defaultTimeZone.getID())
                            .build());
        } catch (Exception e) {
            log.error("Error transforming {} to {}: {}",
                    EPJN_Price.class.getSimpleName(),
                    PriceForHour.class.getSimpleName(),
                    e.getMessage());
            return Optional.empty();
        }

    }

    protected void save(PriceForHour priceForHour) {

        try {
            priceService.save(priceForHour);
            if (log.isTraceEnabled()) {
                log.trace("Price saved: {}", priceForHour);
            }
        } catch (Exception e) {
            throw new DatabaseOperationException("Failed to save Price: " + e.getMessage(), e);
        }
    }

}
