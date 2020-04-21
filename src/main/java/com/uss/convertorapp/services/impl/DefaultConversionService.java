package com.uss.convertorapp.services.impl;

import com.uss.convertorapp.enums.Bases;
import com.uss.convertorapp.exceptions.ProviderNotAvailableException;
import com.uss.convertorapp.services.ConversionRateService;
import com.uss.convertorapp.services.ConversionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Default implementation of {@link ConversionService} which use currency conversion under the
 * hood.
 *
 * @author Iurii Geto
 */
@Service
public class DefaultConversionService implements ConversionService {

  private final ConversionRateService conversionRateService;

  private final Integer scale;

  @Autowired
  public DefaultConversionService(
      @Qualifier("CacheRateConversionProxyService") ConversionRateService conversionRateService,
      @Value("${exchange.scale}") Integer scale) {
    this.conversionRateService = conversionRateService;
    this.scale = scale;
  }

  /**
   * Convert amount based on the bases and init value.
   *
   * @param from  bases
   * @param to    bases
   * @param value which need convert uses from bases
   * @return converted value uses TO bases
   */
  @Override
  public Mono<BigDecimal> convertedAmount(Bases from, Bases to, BigDecimal value) {
    // if from == to, there is no sense to call external services
    if (from == to) {
      return Mono.just(scaleValue(value));
    } else {
      return conversionRateService.getConversionRate(from, to)
          // in case empty stream
          .switchIfEmpty(Mono.error(new ProviderNotAvailableException()))
          .map(currency -> convertValue(currency, value))
          .map(this::scaleValue);
    }
  }

  private BigDecimal convertValue(Float currency, BigDecimal value) {
    return value.multiply(BigDecimal.valueOf(currency));
  }

  private BigDecimal scaleValue(BigDecimal value) {
    return value.setScale(scale, RoundingMode.HALF_EVEN);
  }
}
