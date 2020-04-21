package com.uss.convertorapp.models;

import com.uss.convertorapp.enums.Bases;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Entity uses for rate request.
 *
 * @author Iurii Geto
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConversionRequest {

  @NotNull
  private Bases from;

  @NotNull
  private Bases to;

  @NotNull
  @Min(value = 0, message = "Amount Can't Be negative")
  private BigDecimal amount;
}
