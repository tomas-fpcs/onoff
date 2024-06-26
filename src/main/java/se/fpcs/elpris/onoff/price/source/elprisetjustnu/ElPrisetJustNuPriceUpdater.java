package se.fpcs.elpris.onoff.price.source.elprisetjustnu;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import se.fpcs.elpris.onoff.date.DateProvider;
import se.fpcs.elpris.onoff.db.DatabaseOperationException;
import se.fpcs.elpris.onoff.price.Price;
import se.fpcs.elpris.onoff.price.PriceRepository;
import se.fpcs.elpris.onoff.price.PriceSource;
import se.fpcs.elpris.onoff.price.PriceUpdaterStatus;
import se.fpcs.elpris.onoff.price.PriceZone;
import se.fpcs.elpris.onoff.price.source.elprisetjustnu.model.ElPrisetJustNuPrice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static se.fpcs.elpris.onoff.date.DateUtil.getHour;
import static se.fpcs.elpris.onoff.date.DateUtil.getLocalDate;

@Service
@Log4j2
@Profile("!test")
public class ElPrisetJustNuPriceUpdater {

    private ElPrisetJustNuClient client;
    private final PriceRepository priceRepository;
    private final DateProvider dateProvider;
    private final PriceUpdaterStatus priceUpdaterStatus;

    public ElPrisetJustNuPriceUpdater(
            ElPrisetJustNuClient client,
            PriceRepository priceRepository,
            DateProvider dateProvider,
            PriceUpdaterStatus priceUpdaterStatus) {
        this.client = client;
        this.priceRepository = priceRepository;
        this.dateProvider = dateProvider;
        this.priceUpdaterStatus = priceUpdaterStatus;
        refreshPrices();
    }

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

            Date now = dateProvider.now();
            Calendar today = Calendar.getInstance();
            today.setTime(now);
            days.add(today);

            if (today.get(Calendar.HOUR_OF_DAY) > 22) {
                // I am not sure of the exact time when tomorrow prices are available, but start trying after 2200
                Calendar tomorrow = Calendar.getInstance();
                tomorrow.setTime(today.getTime());
                tomorrow.add(Calendar.DATE, 1);
                days.add(tomorrow);
            }

            days.stream()
                    .forEach(calendar -> {
                        Arrays.stream(getPrices(priceZone, calendar))
                                .forEach(price -> {
                                    Optional<Price> optionalPrice = toPrice(priceZone, price);
                                    if (optionalPrice.isPresent()) {
                                        save(optionalPrice.get());
                                    }
                                });
                    });
            log.trace("Prices saved");
        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                // this is normal early in the day as prices are not yet set for the next day
                log.trace("Not found: {}", e.getMessage());
            } else {
                log.error("HTTP status: {}: {}", e.getStatusCode(), e.getMessage(), e);
            }
        } catch (Exception e) {
            log.error("Error calling {}: {}", PriceSource.ELPRISETJUSTNU.name(), e.getMessage(), e);
        }

    }

    private ElPrisetJustNuPrice[] getPrices(
            PriceZone priceZone,
            Calendar calendar) {

        final String year = String.valueOf(calendar.get(Calendar.YEAR));
        final String month = String.format("%02d", 1 + calendar.get(Calendar.MONTH));
        final String day = String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH));

        try {
            return client.getPrices(
                    year,
                    month,
                    day,
                    priceZone.name());
        } catch (Exception e) {
            log.error("Failed to get prices for {} {}-{}-{}",
                    priceZone.name(),
                    year,
                    month,
                    day);
            throw new RuntimeException(e);
        }

    }

    private Optional<Price> toPrice(PriceZone priceZone, ElPrisetJustNuPrice price) {

        try {
            return Optional.of(
                    Price.builder()
                            .priceSource(PriceSource.ELPRISETJUSTNU)
                            .priceZone(priceZone)
                            .sekPerKWh(price.getSekPerKWh())
                            .eurPerKWh(price.getEurPerKWh())
                            .exchangeRate(price.getExr())
                            .priceDay(getLocalDate(price.getTimeStart()))
                            .hourOfDay(getHour(price.getTimeStart()))
                            .build());
        } catch (Exception e) {
            log.error("Error transforming ElPrisetJustNuPrice to Price: {}", e.getMessage());
            return Optional.empty();
        }

    }

    private void save(Price price) {

        if (null != priceRepository.findPriceByPriceZoneAndPriceDayAndHourOfDay(
                price.getPriceZone(),
                price.getPriceDay(),
                price.getHourOfDay())) {
            if (log.isTraceEnabled()) {
                log.trace("Price already exist in db: {}", price);
            }
            return;
        }

        try {
            priceRepository.save(price);
            if (log.isTraceEnabled()) {
                log.trace("Price saved: {}", price);
            }
        } catch (Exception e) {
            throw new DatabaseOperationException("Failed to save Price: " + e.getMessage(), e);
        }
    }

}
