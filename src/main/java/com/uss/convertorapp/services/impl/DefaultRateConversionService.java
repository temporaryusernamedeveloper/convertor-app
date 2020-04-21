package com.uss.convertorapp.services.impl;

import com.uss.convertorapp.enums.Bases;
import com.uss.convertorapp.services.ConversionRateService;
import com.uss.convertorapp.services.CurrencyRateProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Default rate conversion service which use providers to make real call and get rate.
 *
 * @author Iurii Geto
 */
@Service("DefaultRateConversionService")
@Slf4j
public class DefaultRateConversionService implements ConversionRateService {

  private final List<CurrencyRateProvider> currencyRateProviders;

  @Autowired
  public DefaultRateConversionService(List<CurrencyRateProvider> currencyRateProviders) {
    this.currencyRateProviders = currencyRateProviders;
    // before running application check, if at least 1 provider available
    Assert.notNull(this.currencyRateProviders, "Missing currency providers");
  }

  /**
   * Get rate using list of providers. Randomly pick one provider, in case error use another
   * provider from list.
   *
   * @param from value e.x. USD
   * @param to   value e.x. EUR
   * @return conversion rate, In case missing rate return empty stream
   */
  @Override
  public Mono<Float> getConversionRate(Bases from, Bases to) {
    // pick random provider
    Collections.shuffle(currencyRateProviders);
    CurrencyRateProvider currencyRateProvider = currencyRateProviders.get(0);
    log.info("Get currency rate");

    return currencyRateProvider.getExchangeCurrency(from)
        // in case error, try another provider as fallback
        .onErrorResume(throwable -> {
          log.warn("Provider do not respond", throwable);
          return currencyRateProviders
              .get(currencyRateProviders.size() - 1)
              .getExchangeCurrency(from);
        })
        .map(ratesMap -> ratesMap.getOrDefault(to.getBase(), null))
        .filter(Objects::nonNull);
  }
}
