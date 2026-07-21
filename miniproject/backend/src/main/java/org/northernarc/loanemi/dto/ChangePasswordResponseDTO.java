package org.northernarc.loanemi.dto;

/**
 * DTO for change password response.
 */
public class ChangePasswordResponseDTO {

    private String message;

    public ChangePasswordResponseDTO() {
    }

    public ChangePasswordResponseDTO(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
