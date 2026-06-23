package org.example.springjpa.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TicketResponseDTO {
     private Integer id;
     private int seatno;
     private double seatfare;
     private Date boardingDate;
     private Time boardingTime;

    public String getStatus(){

        LocalDateTime boarding=LocalDateTime.of(boardingDate.toLocalDate(),boardingTime.toLocalTime());

        return (boarding.isAfter(LocalDateTime.now())) ? "ACTIVE" :"EXPIRED";

    }
}
