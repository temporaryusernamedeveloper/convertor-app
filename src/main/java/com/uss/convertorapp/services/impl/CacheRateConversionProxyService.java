package com.uss.convertorapp.services.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.uss.convertorapp.enums.Bases;
import com.uss.convertorapp.services.ConversionRateService;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.cache.CacheMono;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Signal;

import java.util.concurrent.TimeUnit;

/**
 * Proxy service which support memory caching.
 *
 * @author Iurii Geto
 */
@Service("CacheRateConversionProxyService")
public class CacheRateConversionProxyService implements ConversionRateService {

  private final ConversionRateService conversionRateService;

  private Cache<FromToRequest, Float> myCaffeineCache;

  @Autowired
  public CacheRateConversionProxyService(
      @Qualifier("DefaultRateConversionService") ConversionRateService conversionRateService,
      @Value("${cache.size}") Integer cacheSize,
      @Value("${cache.duration}") Integer cacheDuration) {
    this.conversionRateService = conversionRateService;
    this.myCaffeineCache = Caffeine
        .newBuilder()
        .maximumSize(cacheSize)
        .expireAfterWrite(cacheDuration, TimeUnit.SECONDS)
        .build();
  }

  @Override
  public Mono<Float> getConversionRate(Bases from, Bases to) {
    FromToRequest fromToRequest = new FromToRequest(from, to);
    return findCacheValue(fromToRequest, conversionRateService.getConversionRate(from, to));
  }

  private Mono<Float> findCacheValue(FromToRequest key, Mono<Float> fallBackMono) {
    // use projectreactor addons with caffeine for support reactive cache
    return CacheMono
        .lookup(k -> Mono
            .justOrEmpty(this.myCaffeineCache.getIfPresent(key))
            .map(Signal::next), key)
        .onCacheMissResume(fallBackMono)
        .andWriteWith((k, sig) -> Mono.fromRunnable(() -> writeCacheValue(key, sig.get())));
  }

  private void writeCacheValue(FromToRequest key, Float data) {
    if (data != null) {
      this.myCaffeineCache.put(key, data);
    }
  }

  /**
   * Use inner class as cache key.
   */
  @Getter
  @Setter
  @AllArgsConstructor
  @EqualsAndHashCode
  class FromToRequest {

    private Bases from;

    private Bases to;
  }
}
