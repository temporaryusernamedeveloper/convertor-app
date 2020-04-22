package com.uss.convertorapp.services.impl.providers;

import com.uss.convertorapp.enums.Bases;
import com.uss.convertorapp.exceptions.ProviderException;
import com.uss.convertorapp.services.CurrencyRateProvider;
import lombok.Getter;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Common provider implementation which call webclient and parse code to resource.
 *
 * @author Iurii Geto
 */
public class DefaultCurrencyRateProvider<RESOURCE> implements CurrencyRateProvider {

  private static final String PROVIDERS_NOT_AVAILABLE = "Providers not available";

  private final WebClient webClient;

  private final BiFunction<Bases, UriBuilder, URI> uriBuilder;

  private final Class<RESOURCE> providerResource;

  private final Function<RESOURCE, Map<String, Float>> rateFunction;

  @Getter
  private final Set<Bases> supportedBases;

  public DefaultCurrencyRateProvider(WebClient webClient,
      BiFunction<Bases, UriBuilder, URI> uriBuilder,
      Class<RESOURCE> providerResource,
      Function<RESOURCE, Map<String, Float>> rateFunction, Set<Bases> supportedBases) {
    this.webClient = webClient;
    this.uriBuilder = uriBuilder;
    this.providerResource = providerResource;
    this.rateFunction = rateFunction;
    this.supportedBases = supportedBases;
  }

  @Override
  public Mono<Map<String, Float>> getExchangeCurrency(Bases from) {

    return this.webClient
        .get()
        .uri(uri -> uriBuilder.apply(from, uri))
        .retrieve()
        .bodyToMono(providerResource)
        .onErrorMap(t -> {
          throw new ProviderException(PROVIDERS_NOT_AVAILABLE);
        })
        .map(rateFunction);
  }
}
