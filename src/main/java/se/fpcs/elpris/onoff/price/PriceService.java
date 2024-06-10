package se.fpcs.elpris.onoff.price;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PriceService {

    private final PriceRepository priceRepository;

    public PriceService(PriceRepository priceRepository) {
        this.priceRepository = priceRepository;
    }

    public List<Price> findAll() {
        return priceRepository.findAll();
    }
}
