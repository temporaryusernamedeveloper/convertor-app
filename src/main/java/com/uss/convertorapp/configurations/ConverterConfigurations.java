package com.uss.convertorapp.configurations;

import com.uss.convertorapp.enums.Bases;
import com.uss.convertorapp.models.providers.CurrencyProviderResourceCOM;
import com.uss.convertorapp.models.providers.CurrencyProviderResourceIO;
import com.uss.convertorapp.services.CurrencyRateProvider;
import com.uss.convertorapp.services.impl.providers.DefaultCurrencyRateProvider;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.util.Set;

/**
 * @author Iurii Geto
 */
@Configuration
public class ConverterConfigurations {

  @Bean(name = "webClientIO")
  public WebClient getWebClientIO(@Value("${io.provider.url}") String baseUri,
      @Value("${io.provider.connect_timeout_millis}") Integer timeout) {
    HttpClient httpClient = HttpClient
        .create()
        .tcpConfiguration(client -> client
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeout)
            .doOnConnected(conn -> conn
                .addHandlerLast(new ReadTimeoutHandler(10))
                .addHandlerLast(new WriteTimeoutHandler(10))));

    ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);

    return WebClient
        .builder()
        .baseUrl(baseUri)
        //e.x. Could be added auth here or on conecter level
//        .filter(ExchangeFilterFunctions
//            .basicAuthentication(username, token))
        .clientConnector(connector)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }

  @Bean(name = "webClientCom")
  public WebClient getWebClientCom(@Value("${com.provider.url}") String baseUri,
      @Value("${com.provider.connect_timeout_millis}") Integer timeout) {
    HttpClient httpClient = HttpClient
        .create()
        .tcpConfiguration(client -> client
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeout)
            .doOnConnected(conn -> conn
                .addHandlerLast(new ReadTimeoutHandler(10))
                .addHandlerLast(new WriteTimeoutHandler(10))));

    ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);

    return WebClient
        .builder()
        .baseUrl(baseUri)
        .clientConnector(connector)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }

  /**
   * Real implementation with which integrates with 'https://api.exchangeratesapi.io/latest?base=USD'.
   *
   * @param webClient client which configured for link above
   * @return provider implementation
   */
  @Bean("CurrencyRateProviderIO")
  public CurrencyRateProvider getCurrencyRateProviderIO(
      @Qualifier("webClientIO") WebClient webClient) {
    return new DefaultCurrencyRateProvider<>(webClient, (from, uriBuilder) -> uriBuilder
        .queryParam("base", from.getBase())
        .build(), CurrencyProviderResourceIO.class, CurrencyProviderResourceIO::getRates,
        Set.of(Bases.USD, Bases.EUR, Bases.CAD, Bases.PLN, Bases.GBP));
  }

  /**
   * Real implementation with which integrates with 'https://api.exchangerate-api.com/v4/latest'.
   *
   * @param webClient client which configured for link above
   * @return provider implementation
   */
  @Bean("CurrencyRateProviderCom")
  public CurrencyRateProvider getCurrencyRateProviderCom(
      @Qualifier("webClientCom") WebClient webClient) {
    return new DefaultCurrencyRateProvider<>(webClient, (from, uriBuilder) -> uriBuilder
        .path("/" + from.getBase())
        .build(), CurrencyProviderResourceCOM.class, CurrencyProviderResourceCOM::getRates,
        Set.of(Bases.USD, Bases.EUR, Bases.CAD, Bases.PLN, Bases.GBP, Bases.UAH));
  }
}
