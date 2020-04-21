package com.uss.convertorapp.rest.controllers;

import com.uss.convertorapp.models.ConversionRequest;
import com.uss.convertorapp.models.ConversionResource;
import com.uss.convertorapp.services.ConversionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

/**
 * Main controller which responsible for operation for currency conversion.
 *
 * @author Iurii Geto
 */
@RestController
@Slf4j
@RequestMapping(path = "/currency/converter")
public class ConversionController {

  private final ConversionService conversionService;

  @Autowired
  public ConversionController(ConversionService conversionService) {
    this.conversionService = conversionService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.OK)
  public Mono<ConversionResource> convert(
      @RequestBody @Validated ConversionRequest conversionRequest) {
    log.info("Convert from: {} to: {} amount: {}", conversionRequest.getFrom(),
        conversionRequest.getTo(), conversionRequest.getAmount());
    return conversionService
        .convertedAmount(conversionRequest.getFrom(), conversionRequest.getTo(),
            conversionRequest.getAmount())
        .map(convertedAmount -> buildResponse(conversionRequest, convertedAmount));
  }

  private ConversionResource buildResponse(ConversionRequest conversionRequest,
      BigDecimal convertedAmount) {
    return ConversionResource
        .builder()
        .amount(conversionRequest.getAmount())
        .from(conversionRequest.getFrom())
        .to(conversionRequest.getTo())
        .converted(convertedAmount)
        .build();
  }

}
