package se.fpcs.elpris.onoff.price.source.elprisetjustnu;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import se.fpcs.elpris.onoff.db.DatabaseOperationException;
import se.fpcs.elpris.onoff.price.PriceForHour;
import se.fpcs.elpris.onoff.price.PriceService;
import se.fpcs.elpris.onoff.price.PriceSource;
import se.fpcs.elpris.onoff.price.PriceUpdaterStatus;
import se.fpcs.elpris.onoff.price.PriceZone;
import se.fpcs.elpris.onoff.price.source.elprisetjustnu.model.ElPrisetJustNuPrice;

import java.util.Calendar;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ElPrisetJustNuPriceUpdaterTest {

    @Mock
    private ElPrisetJustNuClient client;

    @Mock
    private PriceService priceService;

    @Mock
    private PriceUpdaterStatus priceUpdaterStatus;

    private ElPrisetJustNuPriceUpdater priceUpdater;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        priceUpdater = new ElPrisetJustNuPriceUpdater(client, priceService, priceUpdaterStatus);
    }

    @Test
    void refreshPrices_shouldCallGetContentForEachPriceZone() {
        priceUpdater.refreshPrices();

        for (PriceZone priceZone : PriceZone.values()) {
            verify(client, atLeastOnce()).getPrices(anyString(), anyString(), anyString(), eq(priceZone.name()));
        }

        verify(priceUpdaterStatus).setReady(PriceSource.ELPRISETJUSTNU);
    }

    @Test
    void getPrices_shouldReturnPrices_whenClientReturnsValidData() {
        ElPrisetJustNuPrice[] mockPrices = new ElPrisetJustNuPrice[]{new ElPrisetJustNuPrice()};
        when(client.getPrices(anyString(), anyString(), anyString(), anyString())).thenReturn(mockPrices);

        Calendar calendar = Calendar.getInstance();
        Optional<ElPrisetJustNuPrice[]> result = priceUpdater.getPrices(PriceZone.SE1, calendar);

        assertTrue(result.isPresent());
        assertEquals(mockPrices, result.get());
    }

    @Test
    void getPrices_shouldHandleExceptions_andReturnEmptyOptional() {
        when(client.getPrices(anyString(), anyString(), anyString(), anyString()))
                .thenThrow(new RuntimeException("Test exception"));

        Calendar calendar = Calendar.getInstance();
        Optional<ElPrisetJustNuPrice[]> result = priceUpdater.getPrices(PriceZone.SE1, calendar);

        assertFalse(result.isPresent());
    }

    @Test
    void toPrice_shouldTransformElPrisetJustNuPriceToPriceForHour() {
        ElPrisetJustNuPrice mockElPrice = ElPrisetJustNuPrice.builder()
                .sekPerKWh(1.5)
                .eurPerKWh(0.15)
                .exr(10.0)
                .timeStart("" + System.currentTimeMillis())
                .build();

        Optional<PriceForHour> result = priceUpdater.toPrice(PriceZone.SE1, mockElPrice);

        assertTrue(result.isPresent());
        PriceForHour priceForHour = result.get();
        assertEquals(PriceZone.SE1, priceForHour.getPriceZone());
        assertEquals(PriceSource.ELPRISETJUSTNU, priceForHour.getPriceSource());
    }

    @Test
    void save_shouldCallPriceServiceSave() {
        PriceForHour mockPrice = PriceForHour.builder().build();

        priceUpdater.save(mockPrice);

        verify(priceService).save(mockPrice);
    }

    @Test
    void save_shouldThrowDatabaseOperationException_onSaveFailure() {
        PriceForHour mockPrice = PriceForHour.builder().build();
        doThrow(new RuntimeException("Database error")).when(priceService).save(mockPrice);

        assertThrows(DatabaseOperationException.class, () -> priceUpdater.save(mockPrice));
    }
}
