package com.uss.convertorapp.services;

import com.uss.convertorapp.enums.Bases;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

/**
 * Service which responsible for currency conversions.
 *
 * @author Iurii Geto
 */
public interface ConversionService {

  /**
   * Convert amount based on the bases and init value.
   *
   * @param from init base
   * @param to another base
   * @param value base on init value
   * @return return converted amount
   */
  Mono<BigDecimal> convertedAmount(Bases from, Bases to, BigDecimal value);

}
