package se.fpcs.elpris.onoff;

import org.springframework.security.core.userdetails.UserDetails;
import se.fpcs.elpris.onoff.price.PriceZone;
import se.fpcs.elpris.onoff.user.User;

public interface OnOffService {

  OnOffResponse on(
      PriceZone priceZone,
      int markupPercent,
      int maxPriceOre,
      UserDetails userDetails);

}
