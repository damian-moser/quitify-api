package ch.gibb.quitify.enums;

import lombok.Getter;

@Getter
public enum Currency {
    EUR("€"),
    USD("USD"),
    GBP("£"),
    JPY("¥"),
    CHF("CHF"),
    AUD("AUD"),
    CAD("CAD"),
    CNY("CNY");

    private final String symbol;

    Currency(String symbol) {
        this.symbol = symbol;
    }

    public static Currency fromSymbol(String symbol) {
        for (Currency currency : values()) {
            if (currency.getSymbol().equalsIgnoreCase(symbol)) {
                return currency;
            }
        }
        throw new IllegalArgumentException("Unknown currency symbol: " + symbol);
    }
}
