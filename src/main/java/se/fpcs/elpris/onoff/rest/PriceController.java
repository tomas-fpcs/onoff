package se.fpcs.elpris.onoff.rest;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import se.fpcs.elpris.onoff.price.PriceService;

import static se.fpcs.elpris.onoff.ApiVersion.ONOFF_V1;


@Hidden
@RestController
@Log4j2
public class PriceController {

    private PriceService priceService;

    public PriceController(PriceService priceService) {
        this.priceService = priceService;
    }

    @GetMapping(value = ONOFF_V1 +"/price")
    @SuppressWarnings("java:S1452")
    public ResponseEntity<?> findAll() {

        log.info("findAll");

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(priceService.findAll());

    }

}


