package se.fpcs.elpris.onoff.rest;

import org.springframework.stereotype.Component;
import se.fpcs.elpris.onoff.OnOffService;
import se.fpcs.elpris.onoff.price.PriceSource;
import se.fpcs.elpris.onoff.price.source.elprisetjustnu.EPJN_OnOffService;

/**
 * This class is in preparation to support multiple price provider
 */
@Component
public class OnOffServiceProvider {

    private final EPJN_OnOffService epjnOnOffService;

    public OnOffServiceProvider(
            EPJN_OnOffService epjnOnOffService
    ) {
        this.epjnOnOffService = epjnOnOffService;
    }

    @SuppressWarnings("java:S112")
    public OnOffService get(PriceSource priceSource) {

        priceSource = priceSource == null ?
                PriceSource.ELPRISETJUSTNU :
                priceSource;

        if (priceSource == PriceSource.ELPRISETJUSTNU) {
            return epjnOnOffService;
        }

        /**
         * should never happen as this method would be updated to support any new values in the enum
         */
        throw new RuntimeException("Unsupported priceSource: " + priceSource);
    }

}
