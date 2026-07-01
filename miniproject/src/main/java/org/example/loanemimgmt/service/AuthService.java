package org.example.loanemimgmt.service;

import org.example.loanemimgmt.dto.AuthResponseDTO;
import org.example.loanemimgmt.dto.LoginRequestDTO;
import org.example.loanemimgmt.dto.RegisterRequestDTO;

public interface AuthService {

    AuthResponseDTO login(LoginRequestDTO request);

    String register(RegisterRequestDTO request);
}

