package org.northernarc.week5_assess.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private Long timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
}

