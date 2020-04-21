package com.uss.convertorapp.exceptions;

/**
 * @author Iurii Geto
 */
public class ProviderNotAvailableException extends RuntimeException {

  public ProviderNotAvailableException() {
    super("Providers not available");
  }
}
