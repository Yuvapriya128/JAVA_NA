package org.example.springjpa.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class FlightUpdateDTO {

    private Integer id;

    @NotBlank(message = "Source is required")
    private String src;
    @NotBlank(message = "Destination is required")
    private String dest;
    @NotNull(message = "Date of departure is required")
    private Date dod;
    @NotNull(message = "Date of arrival is required")
    private Date doa;
    @NotNull(message = "Time of departure is required")
    private Time tod;
    @NotNull(message = "Time of arrival is required")
    private Time toa;

    @AssertTrue(message = "Arrival should come after departure")
    public boolean isvalid(){
        LocalDateTime departure=LocalDateTime.of(dod.toLocalDate(),tod.toLocalTime());
        LocalDateTime arrival=LocalDateTime.of(doa.toLocalDate(),toa.toLocalTime());

        return arrival.isAfter(departure);
    }

}
