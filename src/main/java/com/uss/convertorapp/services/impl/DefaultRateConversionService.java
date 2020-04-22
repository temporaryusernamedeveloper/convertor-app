package com.uss.convertorapp.services.impl;

import com.uss.convertorapp.enums.Bases;
import com.uss.convertorapp.exceptions.ProviderException;
import com.uss.convertorapp.services.ConversionRateService;
import com.uss.convertorapp.services.CurrencyRateProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Default rate conversion service which use providers to make real call and get rate. This class
 * support many providers.(one or more).
 *
 * @author Iurii Geto
 */
@Service("DefaultRateConversionService")
@Slf4j
public class DefaultRateConversionService implements ConversionRateService {

  private static final String PROVIDERS_NOT_BASES = "Providers do not support bases combination";

  private final Map<Pair<Bases, Bases>, List<CurrencyRateProvider>> basesPairToProvider;

  @Autowired
  public DefaultRateConversionService(List<CurrencyRateProvider> currencyRateProviders) {
    // before running application check, if at least 1 provider available
    Assert.notNull(currencyRateProviders, "Missing currency providers");

    // create combination of bases to providers
    Map<Pair<Bases, Bases>, List<CurrencyRateProvider>> pairListMap = new HashMap<>();
    for (CurrencyRateProvider currencyRateProvider : currencyRateProviders) {
      for (Pair<Bases, Bases> basesBasesPair : currencyRateProvider.getBasePairs()) {
        pairListMap
            .computeIfAbsent(basesBasesPair, x -> new ArrayList<>())
            .add(currencyRateProvider);
      }
    }
    this.basesPairToProvider = pairListMap;
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
    Mono<Float> conversionResult;
    List<CurrencyRateProvider> currencyRateProviders = basesPairToProvider.get(Pair.of(from, to));

    if (CollectionUtils.isEmpty(currencyRateProviders)) {
      conversionResult = Mono.empty();
    } else {
      // pick random provider
      Collections.shuffle(currencyRateProviders);
      CurrencyRateProvider currencyRateProvider =
          CollectionUtils.firstElement(currencyRateProviders);

      log.info("Get currency rate");
      conversionResult = currencyRateProvider.getExchangeCurrency(from)
          // in case error, try another provider as fallback
          .onErrorResume(throwable -> {
            log.warn("Provider do not respond", throwable);
            return CollectionUtils
                .lastElement(currencyRateProviders)
                .getExchangeCurrency(from);
          })
          .map(ratesMap -> ratesMap.getOrDefault(to.getBase(), null));
    }
    return conversionResult.filter(Objects::nonNull)
        // in case empty stream
        .switchIfEmpty(Mono.error(new ProviderException(PROVIDERS_NOT_BASES)));
  }
}
