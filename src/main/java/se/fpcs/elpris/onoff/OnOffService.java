package se.fpcs.elpris.onoff;

import se.fpcs.elpris.onoff.price.PriceZone;
import se.fpcs.elpris.onoff.user.User;

public interface OnOffService {

    OnOff on(
            PriceZone priceZone,
            int markupPercent,
            int maxPriceOre,
            User user);

}
