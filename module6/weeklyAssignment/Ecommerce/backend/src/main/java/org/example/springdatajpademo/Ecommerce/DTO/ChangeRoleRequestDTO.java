package org.example.springdatajpademo.Ecommerce.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.springdatajpademo.Ecommerce.model.UserRole;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeRoleRequestDTO {

    @NotNull(message = "Role is required")
    private UserRole role;
}

