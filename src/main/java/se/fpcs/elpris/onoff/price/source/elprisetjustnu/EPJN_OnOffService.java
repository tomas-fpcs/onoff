package se.fpcs.elpris.onoff.price.source.elprisetjustnu;

import static se.fpcs.elpris.onoff.price.source.elprisetjustnu.EPJN_DateUtil.toHour;
import static se.fpcs.elpris.onoff.price.source.elprisetjustnu.EPJN_DateUtil.toYYYYMMDD;

import java.util.Date;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import se.fpcs.elpris.onoff.OnOff;
import se.fpcs.elpris.onoff.OnOffService;
import se.fpcs.elpris.onoff.price.PriceForHour;
import se.fpcs.elpris.onoff.price.PriceNotFoundException;
import se.fpcs.elpris.onoff.price.PriceService;
import se.fpcs.elpris.onoff.price.PriceSource;
import se.fpcs.elpris.onoff.price.PriceUpdaterStatus;
import se.fpcs.elpris.onoff.price.PriceZone;
import se.fpcs.elpris.onoff.price.PricesNotRetrievedYetException;
import se.fpcs.elpris.onoff.user.User;

@Service
@RequiredArgsConstructor
@Log4j2
public class EPJN_OnOffService implements OnOffService {

  private final PriceService priceService;
  private final PriceUpdaterStatus priceUpdaterStatus;

  @Override
  public OnOff on(
      final PriceZone priceZone,
      final int markupPercent,
      final int maxPriceOre,
      final User user) {

    if (!priceUpdaterStatus.isReady(PriceSource.ELPRISETJUSTNU)) {
      throw new PricesNotRetrievedYetException();
    }

    final Date dateNow = new Date();
    Optional<PriceForHour> optionalPriceForHour =
        priceService.findPriceByPriceZoneAndPriceDayAndPriceHour(
            PriceSource.ELPRISETJUSTNU,
            priceZone,
            toYYYYMMDD(dateNow),
            toHour(dateNow));

    if (optionalPriceForHour.isEmpty()) {
      throw new PriceNotFoundException();
    }

    final float priceSpotFloat = optionalPriceForHour.get()
        .getSekPerKWh().floatValue()
        * 100.0f;

    final float markupFactor = 1.0f + (markupPercent / 100f);

    final int priceSupplierOre = Math.round(markupFactor * priceSpotFloat);

    return OnOff.builder()
        .on(priceSupplierOre <= maxPriceOre)
        .maxPrice(maxPriceOre)
        .priceSpot(Math.round(priceSpotFloat))
        .priceSupplier(priceSupplierOre)
        .serverTime(dateNow.toString())
        .build();

  }

}
