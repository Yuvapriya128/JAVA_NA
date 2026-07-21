package org.northernarc.week5_assess.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {

	private Long id;
	private Long accountId;
	private BigDecimal amount;
	private String type;
	private LocalDateTime timestamp;
	private String description;
}

