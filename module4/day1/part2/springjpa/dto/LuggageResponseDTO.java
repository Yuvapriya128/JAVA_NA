package org.example.springjpa.dto;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LuggageResponseDTO {
    private Integer id;

    private double weight;

    private double fare;
}
