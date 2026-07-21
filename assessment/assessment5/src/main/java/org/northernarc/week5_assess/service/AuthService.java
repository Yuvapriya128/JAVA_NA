package org.northernarc.week5_assess.service;

import org.northernarc.week5_assess.dto.AuthRequestDto;
import org.northernarc.week5_assess.dto.AuthResponseDto;

public interface AuthService {

	AuthResponseDto register(AuthRequestDto authRequestDto);

	AuthResponseDto login(AuthRequestDto authRequestDto);
}

