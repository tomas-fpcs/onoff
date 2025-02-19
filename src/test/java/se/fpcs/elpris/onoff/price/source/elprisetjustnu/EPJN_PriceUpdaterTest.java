package se.fpcs.elpris.onoff.price.source.elprisetjustnu;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Calendar;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import se.fpcs.elpris.onoff.db.DatabaseOperationException;
import se.fpcs.elpris.onoff.price.PriceForHour;
import se.fpcs.elpris.onoff.price.PriceRepository;
import se.fpcs.elpris.onoff.price.PriceSource;
import se.fpcs.elpris.onoff.price.PriceUpdaterStatus;
import se.fpcs.elpris.onoff.price.PriceZone;
import se.fpcs.elpris.onoff.price.source.elprisetjustnu.model.EPJN_Price;

class EPJN_PriceUpdaterTest {

  @Mock
  private EPJN_Client client;

  @Mock
  private PriceRepository priceRepository;

  @Mock
  private PriceUpdaterStatus priceUpdaterStatus;

  private EPJN_PriceUpdater priceUpdater;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    priceUpdater = new EPJN_PriceUpdater(client, priceRepository, priceUpdaterStatus);
  }

  @Test
  void shouldCallGetContentForEachPriceZone() {

    priceUpdater.refreshPrices();

    for (PriceZone priceZone : PriceZone.values()) {
      verify(client, atLeastOnce()).getPrices(anyString(), anyString(), anyString(),
          eq(priceZone.name()));
    }

    verify(priceUpdaterStatus).setReady(PriceSource.ELPRISETJUSTNU);
  }

  @Test
  void getPrices_shouldReturnPrices_whenClientReturnsValidData() {
    EPJN_Price[] mockPrices = new EPJN_Price[]{new EPJN_Price()};
    when(client.getPrices(anyString(), anyString(), anyString(), anyString())).thenReturn(
        mockPrices);

    Calendar calendar = Calendar.getInstance();
    Optional<EPJN_Price[]> result = priceUpdater.getPrices(PriceZone.SE1, calendar);

    assertTrue(result.isPresent());
    assertEquals(mockPrices, result.get());
  }

  @Test
  void shouldHandleExceptionsAndReturnEmptyOptional() {

    when(client.getPrices(anyString(), anyString(), anyString(), anyString()))
        .thenThrow(new RuntimeException("Test exception"));

    Optional<EPJN_Price[]> result = priceUpdater.getPrices(PriceZone.SE1, Calendar.getInstance());

    assertTrue(result.isEmpty());
  }

  @Test
  void shouldTransformEPJNToPriceForHour() {

    EPJN_Price mockPrice = EPJN_Price.builder()
        .sekPerKWh(1.5)
        .eurPerKWh(0.15)
        .exr(10.0)
        .timeStart("2025-01-20T00:00:00+01:00")
        .build();

    Optional<PriceForHour> result = priceUpdater.toPrice(PriceZone.SE1, mockPrice);

    assertTrue(result.isPresent());
    PriceForHour priceForHour = result.get();
    assertEquals(PriceZone.SE1, priceForHour.getPriceZone());
    assertEquals(PriceSource.ELPRISETJUSTNU, priceForHour.getPriceSource());
    assertEquals(1737327600000L, priceForHour.getPriceTimeMs());
  }

  @Test
  void shouldCallPriceServiceSave() {

    PriceForHour mockPrice = PriceForHour.builder().build();
    priceUpdater.save(mockPrice);
    verify(priceRepository).save(mockPrice);
  }

  @Test
  void shouldThrowDatabaseOperationExceptionOnSaveFailure() {

    PriceForHour mockPrice = PriceForHour.builder().build();
    doThrow(new RuntimeException("Database error")).when(priceRepository).save(mockPrice);
    assertThrows(DatabaseOperationException.class, () -> priceUpdater.save(mockPrice));

  }
}
