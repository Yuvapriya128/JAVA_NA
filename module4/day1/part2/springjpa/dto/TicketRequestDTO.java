package org.example.springjpa.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TicketRequestDTO {

    @Min(value =1,message = "Seat no. should be positive")
    private int seatno;
    @Positive(message = "Fare should be positive")
    private double seatfare;
    @NotNull(message = "Boarding Date is blank")
    private Date boardingDate;
    @NotNull(message = "Boarding time is blank")
    private Time boardingTime;

}
