package org.example.springjpa.dto;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LuggageRequestDTO {
   @Positive(message = "weight > 0")
    private double weight;
   @Positive(message = "fare > 0")
    private double fare;
}
