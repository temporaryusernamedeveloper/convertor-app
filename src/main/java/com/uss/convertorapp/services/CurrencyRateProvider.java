package com.uss.convertorapp.services;

import com.uss.convertorapp.enums.Bases;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Interface represent provider which integrate with real system to get currency.
 *
 * @author Iurii Geto
 */
public interface CurrencyRateProvider {

  Mono<Map<String, Float>> getExchangeCurrency(Bases from);

}
