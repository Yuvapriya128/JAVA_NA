package org.example.loanemimgmt.dto;

import org.example.loanemimgmt.enums.UserRole;

public record AuthResponseDTO(
        String token,
        String tokenType,
        String email,
        UserRole role
) {
}

