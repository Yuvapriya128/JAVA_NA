package org.example.springjpa.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightResponseDTO {
    private Integer id;
    private String src;
    private String dest;
    private Date dod;
    private Date doa;
    private Time tod;
    private Time toa;


}
