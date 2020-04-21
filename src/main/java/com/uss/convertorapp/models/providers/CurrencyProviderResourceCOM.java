package com.uss.convertorapp.models.providers;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * Currency provider resource for this url: https://api.exchangerate-api.com/v4/latest/EUR.
 *
 * @author Iurii Geto
 */
@Getter
@Setter
public class CurrencyProviderResourceCOM {

  private String base;

  private Map<String, Float> rates;
}
