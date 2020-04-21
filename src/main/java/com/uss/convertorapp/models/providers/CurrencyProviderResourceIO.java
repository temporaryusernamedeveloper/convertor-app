package com.uss.convertorapp.models.providers;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * Currency provider resource for this url: https://api.exchangeratesapi.io/latest?base=EUR.
 *
 * @author Iurii Geto
 */
@Getter
@Setter
public class CurrencyProviderResourceIO {

  private String base;

  private Map<String, Float> rates;

}
