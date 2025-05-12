package org.example.calc_client;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
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
