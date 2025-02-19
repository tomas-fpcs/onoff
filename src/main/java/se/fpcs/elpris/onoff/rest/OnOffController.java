package se.fpcs.elpris.onoff.rest;

import static se.fpcs.elpris.onoff.Constants.ONOFF_V1;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import se.fpcs.elpris.onoff.OnOffResponse;
import se.fpcs.elpris.onoff.price.PriceSource;
import se.fpcs.elpris.onoff.price.PriceZone;
import se.fpcs.elpris.onoff.validation.ValidEnum;

@RestController
@RequiredArgsConstructor
@Log4j2
public class OnOffController {

  private final OnOffServiceProvider onOffServiceProvider;

  @Operation(summary = "Determine if device should be on")
  @ApiResponse(responseCode = "200",
      content = {@Content(mediaType = "application/json",
          schema = @Schema(implementation = OnOffResponse.class))})
  @GetMapping(value = ONOFF_V1 + "/onoff")
  @ResponseBody
  @SuppressWarnings("java:S1452")
  public ResponseEntity<?> onoff(
      @Parameter(description = "The source of the spot prices, currently https://www.elprisetjustnu.se is the only supported source")
      @RequestParam(name = "price_source", required = false)
      @ValidEnum(enumClass = PriceSource.class, allowNull = true) PriceSource priceSource,

      @Parameter(description = "The price zone (swedish: elområde)", required = true)
      @RequestParam("price_zone") @ValidEnum(enumClass = PriceZone.class) PriceZone priceZone,

      @Parameter(description = "The maximum price for the device to be on (in öre)", required = true)
      @RequestParam("max_price") @Min(0) Integer maxPriceOre,

      @Parameter(description = "The markup added to the spot price by your provider, in percent.", required = true)
      @RequestParam("markup_percent") @Min(0) Integer markupPercent,

      @Parameter(description = "The output format")
      @RequestParam(name = "output_type", required = false)
      @ValidEnum(enumClass = OutputType.class, allowNull = true) OutputType outputType
  ) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null ||
        !(authentication.getPrincipal() instanceof UserDetails)) {
      throw new IllegalStateException("UserDetails not found");
    }

    OnOffResponse onOffResponse = onOffServiceProvider.get(priceSource)
        .on(priceZone,
            markupPercent,
            maxPriceOre,
            (UserDetails) authentication.getPrincipal());

    if (outputType == null || outputType == OutputType.JSON) {
      return ResponseEntity.ok()
          .contentType(MediaType.APPLICATION_JSON)
          .body(onOffResponse);
    } //
    else if (outputType == OutputType.MINIMALIST) {
      return ResponseEntity.ok()
          .contentType(MediaType.TEXT_PLAIN)
          .body(onOffResponse.isOn());
    } //
    else {
      return ResponseEntity.ok()
          .contentType(MediaType.TEXT_PLAIN)
          .body(toText(onOffResponse));
    }

  }

  public static String toText(OnOffResponse onOffResponse) {

    return String.format("on=%s;max-price=%s;price-spot=%s;price-supplier=%s;user-name=%s",
        onOffResponse.isOn(),
        onOffResponse.getMaxPrice(),
        onOffResponse.getPriceSpot(),
        onOffResponse.getPriceSupplier(),
        onOffResponse.getUserName());

  }

}
