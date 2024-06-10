package se.fpcs.elpris.onoff.price;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
@RepositoryRestResource(exported = false)
public interface PriceRepository extends JpaRepository<Price, Long> {

    @Query("SELECT p FROM Price p WHERE p.priceZone = :priceZone AND p.priceDay = :priceDay AND p.hourOfDay = :hourOfDay")
    Price findPriceByPriceZoneAndPriceDayAndHourOfDay(
            @Param("priceZone") PriceZone priceZone,
            @Param("priceDay") LocalDate priceDay,
            @Param("hourOfDay") Integer hourOfDay);

}
