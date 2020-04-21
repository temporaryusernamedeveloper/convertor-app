package com.uss.convertorapp.services.impl.providers;

import com.uss.convertorapp.enums.Bases;
import com.uss.convertorapp.models.providers.CurrencyProviderResourceIO;
import com.uss.convertorapp.services.CurrencyRateProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Real implementation with which integrates with 'https://api.exchangeratesapi.io/latest?base=USD'.
 *
 * @author Iurii Geto
 * @deprecated Created common solution {@link DefaultCurrencyRateProvider}, which can be configured
 * from configuration as a result no sense to duplicate same code many times.
 */

@Deprecated
public class CurrencyRateProviderIO implements CurrencyRateProvider {

  private final WebClient webClient;

  @Autowired
  public CurrencyRateProviderIO(@Qualifier("webClientIO") WebClient webClientIO) {
    this.webClient = webClientIO;
  }

  @Override
  public Mono<Map<String, Float>> getExchangeCurrency(Bases from) {
    return webClient
        .get()
        .uri(uriBuilder -> uriBuilder
            .queryParam("base", from.getBase())
            .build())
        .retrieve()
        .bodyToMono(CurrencyProviderResourceIO.class)
        .map(CurrencyProviderResourceIO::getRates);
  }
}
