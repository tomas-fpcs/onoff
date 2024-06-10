package se.fpcs.elpris.onoff.price.source.elprisetjustnu;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.fpcs.elpris.onoff.OnOff;
import se.fpcs.elpris.onoff.OnOffService;
import se.fpcs.elpris.onoff.price.PriceSource;
import se.fpcs.elpris.onoff.date.DateProvider;
import se.fpcs.elpris.onoff.price.Price;
import se.fpcs.elpris.onoff.price.PriceRepository;
import se.fpcs.elpris.onoff.price.PriceUpdaterStatus;
import se.fpcs.elpris.onoff.price.PriceZone;
import se.fpcs.elpris.onoff.security.User;

import java.math.BigDecimal;
import java.util.Date;

import static java.util.Objects.requireNonNull;
import static se.fpcs.elpris.onoff.date.DateUtil.getHour;
import static se.fpcs.elpris.onoff.date.DateUtil.getLocalDate;

@Service
@Log4j2
public class OnOffServiceElprisetJustNu implements OnOffService {

    private final PriceRepository priceRepository;
    private final DateProvider dateProvider;
    private final PriceUpdaterStatus priceUpdaterStatus;

    @Autowired
    public OnOffServiceElprisetJustNu(
            PriceUpdaterStatus priceUpdaterStatus,
            PriceRepository priceRepository,
            DateProvider dateProvider) {
        this.priceUpdaterStatus = priceUpdaterStatus;
        this.priceRepository = requireNonNull(priceRepository);
        this.dateProvider = dateProvider;
    }

    @Override
    public OnOff on(
            final PriceZone priceZone,
            final int markupPercent,
            final int maxPriceOre,
            final User user) {

        if (!priceUpdaterStatus.isReady(PriceSource.ELPRISETJUSTNU)) {
            throw new PricesNotRetrievedYetException();
        }

        Date now = dateProvider.now();
        log.info("PriceZone: {} Max Price: {} Now: {} getLocalDate: {} getHour: {}",
                priceZone, maxPriceOre, now, getLocalDate(now), getHour(now));

        Price price = priceRepository.findPriceByPriceZoneAndPriceDayAndHourOfDay(
                priceZone, getLocalDate(now), getHour(now));

        final float priceSpotFloat = price.getSekPerKWh()
                .multiply(BigDecimal.valueOf(100.0f)).floatValue();

        final float markupFactor = 1.0f + (markupPercent / 100f);

        final int priceSupplierOre = Math.round(markupFactor * priceSpotFloat);

        return OnOff.builder()
                .on(priceSupplierOre <= maxPriceOre)
                .maxPrice(maxPriceOre)
                .priceSpot(Math.round(priceSpotFloat))
                .priceSupplier(priceSupplierOre)
                .userName(user.getName())
                .build();

    }

}
