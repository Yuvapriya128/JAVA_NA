package org.northernarc.week5_assess.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {

	private Long id;

	@NotBlank
	private String accountNumber;

	@NotNull
	@DecimalMin("0.0")
	private BigDecimal openingBalance;

	private String accountType;

	@NotNull
	private Long customerId;
}

