package com.uss.convertorapp.enums;

/**
 * List of supported bases.
 *
 * @author Iurii Geto
 */
public enum Bases {
  USD("USD"), EUR("EUR"), CAD("CAD"), PLN("PLN"), GBP("GBP"), UAH("UAH");

  private String base;

  Bases(String base) {
    this.base = base;
  }

  public String getBase() {
    return base;
  }
}
