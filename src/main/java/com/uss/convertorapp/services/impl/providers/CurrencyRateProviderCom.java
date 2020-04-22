package com.uss.convertorapp.services.impl.providers;

import com.uss.convertorapp.enums.Bases;
import com.uss.convertorapp.models.providers.CurrencyProviderResourceCOM;
import com.uss.convertorapp.services.CurrencyRateProvider;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Set;

/**
 * Real implementation with which integrates with 'https://api.exchangerate-api.com/v4/latest'.
 * @deprecated Created common solution {@link DefaultCurrencyRateProvider},
 * which can be configured from configuration as a result no sense to duplicate same code many times.
 *
 * @author Iurii Geto
 */
//@Service("CurrencyRateProviderCom")
@Deprecated
public class CurrencyRateProviderCom implements CurrencyRateProvider {

  private final WebClient webClient;

  @Getter
  private final Set<Bases> supportedBases = Set.of(Bases.USD, Bases.EUR);

  @Autowired
  public CurrencyRateProviderCom(@Qualifier("webClientCom") WebClient webClient) {
    this.webClient = webClient;
  }

  @Override
  public Mono<Map<String, Float>> getExchangeCurrency(Bases from) {
    return webClient
        .get()
        .uri(uriBuilder -> uriBuilder
            .path("/" + from.getBase())
            .build())
        .retrieve()
        .bodyToMono(CurrencyProviderResourceCOM.class)
        .map(CurrencyProviderResourceCOM::getRates);
  }
}
