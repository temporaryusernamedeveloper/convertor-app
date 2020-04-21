package com.uss.convertorapp.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.uss.convertorapp.enums.Bases;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Entity uses for rate response.
 *
 * @author Iurii Geto
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConversionResource {

  private Bases from;

  private Bases to;

  private BigDecimal amount;

  private BigDecimal converted;

}
