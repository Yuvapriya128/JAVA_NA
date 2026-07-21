package org.northernarc.loanemi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterResponseDTO {

    private String message;
    private Long userId;
    private String role;

    public static RegisterResponseDTO success(Long userId) {
        return new RegisterResponseDTO("Registration successful", userId, "USER");
    }
}
