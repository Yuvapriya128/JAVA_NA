package org.northernarc.week5_assess.service.impl;

import org.northernarc.week5_assess.dto.AuthRequestDto;
import org.northernarc.week5_assess.dto.AuthResponseDto;
import org.northernarc.week5_assess.service.AuthService;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Override
    public AuthResponseDto register(AuthRequestDto authRequestDto) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public AuthResponseDto login(AuthRequestDto authRequestDto) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}

