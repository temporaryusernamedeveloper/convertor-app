package com.uss.convertorapp.enums;

/**
 * List of supported bases.
 *
 * @author Iurii Geto
 */
public enum Bases {
  USD("USD"), EUR("EUR");

  private String base;

  Bases(String base) {
    this.base = base;
  }

  public String getBase() {
    return base;
  }
}
