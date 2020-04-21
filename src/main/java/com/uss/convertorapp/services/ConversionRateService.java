package com.uss.convertorapp.services;

import com.uss.convertorapp.enums.Bases;
import reactor.core.publisher.Mono;

/**
 * Service which works with bases rates.
 *
 * @author Iurii Geto
 */
public interface ConversionRateService {

  /**
   * Get conversion rate based on params below.
   *
   * @param from value e.x. USD
   * @param to value e.x. EUR
   * @return number result which show rate for two bases
   */
  Mono<Float> getConversionRate(Bases from, Bases to);

}
