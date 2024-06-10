package se.fpcs.elpris.onoff.price.source.elprisetjustnu;

public class PricesNotRetrievedYetException extends RuntimeException {
    public PricesNotRetrievedYetException() {
        super("Prices for the specified price source is still being retrieved.");
    }
}
