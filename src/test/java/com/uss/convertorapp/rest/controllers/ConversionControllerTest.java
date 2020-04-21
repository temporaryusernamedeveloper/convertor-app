package com.uss.convertorapp.rest.controllers;

import static org.hamcrest.Matchers.equalTo;

import com.uss.convertorapp.enums.Bases;
import com.uss.convertorapp.models.ConversionRequest;
import com.uss.convertorapp.models.ConversionResource;
import com.uss.convertorapp.services.ConversionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

/**
 * @author Iurii Geto
 */
@RunWith(SpringRunner.class)
@WebFluxTest(controllers = ConversionController.class)
public class ConversionControllerTest {

  private static final String CURRENCY_CONVERTER_URL = "/currency/converter";

  @MockBean
  private ConversionService conversionService;

  @Autowired
  private WebTestClient webClient;

  @Test
  public void testSuccessConvert() {
    ConversionRequest conversionRequest = getConversionRequest(100f);
    BigDecimal expectedResult = BigDecimal.valueOf(150);
    Mockito
        .when(conversionService.convertedAmount(conversionRequest.getFrom(),
            conversionRequest.getTo(), conversionRequest.getAmount()))
        .thenReturn(Mono.just(expectedResult));

    webClient
        .post()
        .uri(CURRENCY_CONVERTER_URL)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .body(Mono.just(conversionRequest), ConversionRequest.class)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(ConversionResource.class)
        .value(ConversionResource::getConverted, equalTo(expectedResult));
  }

  @Test
  public void testValidationOnNullParams() {
    ConversionRequest conversionRequest = new ConversionRequest();
    webClient
        .post()
        .uri(CURRENCY_CONVERTER_URL)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .body(Mono.just(conversionRequest), ConversionRequest.class)
        .exchange()
        .expectStatus()
        .is4xxClientError();
  }

  @Test
  public void testValidationNegativeAmount() {
    ConversionRequest conversionRequest =  getConversionRequest(-100f);
    webClient
        .post()
        .uri(CURRENCY_CONVERTER_URL)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .body(Mono.just(conversionRequest), ConversionRequest.class)
        .exchange()
        .expectStatus()
        .is4xxClientError();
  }

  private ConversionRequest getConversionRequest(Float amount) {
    ConversionRequest conversionRequest = new ConversionRequest();
    conversionRequest.setFrom(Bases.USD);
    conversionRequest.setTo(Bases.EUR);
    conversionRequest.setAmount(BigDecimal.valueOf(amount));
    return conversionRequest;
  }
}