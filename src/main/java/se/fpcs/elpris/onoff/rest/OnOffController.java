package se.fpcs.elpris.onoff.rest;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.Min;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import se.fpcs.elpris.onoff.OnOff;
import se.fpcs.elpris.onoff.price.PriceSource;
import se.fpcs.elpris.onoff.price.PriceZone;
import se.fpcs.elpris.onoff.security.User;
import se.fpcs.elpris.onoff.validation.ValidEnum;

import static se.fpcs.elpris.onoff.ApiVersion.ONOFF_V1;


@Controller
@SuppressWarnings("java:S6833") // this class is NOT a REST controller
public class OnOffController {

    private OnOffServiceProvider onOffServiceProvider;

    public OnOffController(
            OnOffServiceProvider onOffServiceProvider) {
        this.onOffServiceProvider = onOffServiceProvider;
    }

    //@Operation(summary = "Get user by ID", description = "Retrieve a user by their ID")
    @ApiResponse(responseCode = "200",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = OnOff.class))})
    //@ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    @GetMapping(value = ONOFF_V1 + "/onoff")
    @ResponseBody
    @SuppressWarnings("java:S1452")
    public ResponseEntity<?> onoff(
            @RequestParam(name = "price_source", required = false)
            @ValidEnum(enumClass = PriceSource.class, allowNull = true) PriceSource priceSource,
            @RequestParam("price_zone") @ValidEnum(enumClass = PriceZone.class) PriceZone priceZone,
            @RequestParam("max_price") @Min(0) Integer maxPriceOre,
            @RequestParam("markup_percent") @Min(0) Integer markupPercent,
            @RequestParam(name = "output_type", required = false)
            @ValidEnum(enumClass = OutputType.class, allowNull = true) OutputType outputType
    ) {

        OnOff onOff = onOffServiceProvider.get(priceSource).on(
                priceZone,
                markupPercent,
                maxPriceOre,
                User.builder().name("DevUser").build());

        if (outputType == null || outputType == OutputType.JSON) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(onOff);
        } //
        else if (outputType == OutputType.MINIMALIST) {
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(onOff.isOn());
        } //
        else {
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(toText(onOff));
        }

    }

    public static String toText(OnOff onOff) {

        return String.format("on=%s;max-price=%s;price-spot=%s;price-supplier=%s;user-name=%s",
                onOff.isOn(),
                onOff.getMaxPrice(),
                onOff.getPriceSpot(),
                onOff.getPriceSupplier(),
                onOff.getUserName());

    }

}
