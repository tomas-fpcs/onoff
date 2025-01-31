package se.fpcs.elpris.onoff.rest;

import static se.fpcs.elpris.onoff.Constants.ONOFF_V1;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import se.fpcs.elpris.onoff.price.PriceService;
import se.fpcs.elpris.onoff.price.PriceSource;


@RestController
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Log4j2
public class PriceController {

  @Autowired
  private PriceService priceService;

  @Operation(summary = "Get all prices")
  @GetMapping(value = ONOFF_V1 + "/price")
  @SuppressWarnings("java:S1452")
  public ResponseEntity<?> findAll(
      @RequestParam(value = "priceSource", required = false) PriceSource priceSource
  ) {

    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(priceService.findAll(priceSource));

  }

}


