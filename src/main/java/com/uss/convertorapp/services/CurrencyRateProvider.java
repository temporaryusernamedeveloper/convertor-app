package com.uss.convertorapp.services;

import com.uss.convertorapp.enums.Bases;
import org.apache.commons.lang3.tuple.Pair;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Interface represent provider which integrate with real system to get currency.
 *
 * @author Iurii Geto
 */
public interface CurrencyRateProvider {

  Set<Bases> getSupportedBases();

  Mono<Map<String, Float>> getExchangeCurrency(Bases from);

  default Set<Pair<Bases, Bases>> getBasePairs() {
    List<Bases> bases = new ArrayList<>(getSupportedBases());
    Set<Pair<Bases, Bases>> pairs = new HashSet<>();
    for (int i = 0; i < getSupportedBases().size(); ++i) {
      for (int j = i + 1; j < bases.size(); ++j) {
        pairs.add(Pair.of(bases.get(i), bases.get(j)));
        pairs.add(Pair.of(bases.get(j), bases.get(i)));
      }
    }
    return pairs;
  }

}
