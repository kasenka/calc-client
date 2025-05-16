package org.example.calc_client.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CurrencyRateDTO {
    private Long id;
    private String fromCurrency;
    private String toCurrency;
    private Double rate;
}
