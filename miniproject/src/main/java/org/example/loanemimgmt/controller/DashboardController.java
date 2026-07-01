package org.example.loanemimgmt.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.loanemimgmt.dto.SystemDashboardDTO;
import org.example.loanemimgmt.service.LoanService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "System Dashboard", description = "System-wide aggregate metrics")
@SecurityRequirement(name = "bearerAuth")
public class DashboardController {

    private final LoanService loanService;

    public DashboardController(LoanService loanService) {
        this.loanService = loanService;
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @Operation(summary = "Get system dashboard")
    public SystemDashboardDTO getDashboard() {
        return loanService.getSystemDashboard();
    }
}

