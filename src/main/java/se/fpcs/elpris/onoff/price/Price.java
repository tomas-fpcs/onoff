package se.fpcs.elpris.onoff.price;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "price")
public class Price {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(name = "price_source")
    private PriceSource priceSource;

    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(name = "price_zone")
    private PriceZone priceZone;

    @NonNull
    @Column(name = "sek_per_kwh")
    public BigDecimal sekPerKWh;

    @NonNull
    @Column(name = "eur_per_kwh")
    public BigDecimal eurPerKWh;

    @NonNull
    @Column(name = "exchange_rate")
    public BigDecimal exchangeRate;

    @NonNull
    @Column(name = "price_day")
    public LocalDate priceDay; // stupid name but "day" is reserved in SQL

    @NonNull
    @Column(name = "hour_of_day")
    public Integer hourOfDay;

}
