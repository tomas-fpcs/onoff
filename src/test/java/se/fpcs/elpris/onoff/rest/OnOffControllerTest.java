package se.fpcs.elpris.onoff.rest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import se.fpcs.elpris.onoff.OnOff;
import se.fpcs.elpris.onoff.price.PriceSource;
import se.fpcs.elpris.onoff.price.PriceUpdaterStatus;

import static java.lang.String.format;
import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static se.fpcs.elpris.onoff.ApiVersion.ONOFF_V1;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OnOffControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PriceUpdaterStatus priceUpdaterStatus;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @Sql("/data-OnOffController.sql")
    void shouldGetOnOff() throws Exception {

        given(priceUpdaterStatus.isReady(PriceSource.ELPRISETJUSTNU))
                .willReturn(true);

        {
            final String expected = "{\"on\":false,\"max-price\":50,\"price-spot\":85,\"price-supplier\":92,\"user-name\":\"DevUser\"}";

            mockMvc.perform(get(ONOFF_V1 + "/onoff")
                            .param("price_source", "elprisetjustnu")
                            .param("price_zone", "SE3")
                            .param("price_day", "2024-05-30")
                            .param("hour_of_day", "14")
                            .param("markup_percent", "8")
                            .param("max_price", "50")
                            .param("output_type", "JSON"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(expected));
        }

        {
            final String expected = "{\"price_source\":\"Parameter 'price_source' should be of type 'PriceSource' but the value 'non_existing' is not\"}";

            mockMvc.perform(get(ONOFF_V1 + "/onoff")
                            .param("price_source", "non_existing")
                            .param("price_zone", "SE3")
                            .param("price_day", "2024-05-30")
                            .param("hour_of_day", "14")
                            .param("markup_percent", "8")
                            .param("max_price", "50")
                            .param("output_type", "JSON"))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string(expected));
        }

        {
            final String expected = "{\"on\":false,\"max-price\":50,\"price-spot\":85,\"price-supplier\":92,\"user-name\":\"DevUser\"}";

            mockMvc.perform(get(ONOFF_V1 + "/onoff")
                            // no price_source, expect default
                            .param("price_zone", "SE3")
                            .param("price_day", "2024-05-30")
                            .param("hour_of_day", "14")
                            .param("markup_percent", "8")
                            .param("max_price", "50")
                            .param("output_type", "JSON"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(expected));
        }


    }

    @Test
    void shouldExportToText() {

        final boolean isOn = true;
        final int maxPrice = 100;
        final int priceSupplier = 60;
        final int priceSpot = 50;
        final String userName = "Kalle";

        OnOff onOff = OnOff.builder()
                .on(isOn)
                .maxPrice(maxPrice)
                .priceSpot(priceSpot)
                .priceSupplier(priceSupplier)
                .userName(userName)
                .build();

        assertEquals(
                format("on=%s;max-price=%s;price-spot=%s;price-supplier=%s;user-name=%s",
                        isOn,
                        maxPrice,
                        priceSpot,
                        priceSupplier,
                        userName),
                OnOffController.toText(onOff));

    }
}