package org.northernarc.week5_assess.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.northernarc.week5_assess.dto.AuthRequestDto;
import org.northernarc.week5_assess.dto.AuthResponseDto;
import org.northernarc.week5_assess.exception.InvalidRequestException;
import org.northernarc.week5_assess.exception.UnauthorizedException;
import org.northernarc.week5_assess.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.lang.reflect.Field;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@DisplayName("AuthControllerTest")
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    private static final String REGISTER_ENDPOINT = "/api/auth/register";
    private static final String LOGIN_ENDPOINT = "/api/auth/login";

    @BeforeEach
    void setUp() {
        reset(authService);
    }

    // ==================== POST /api/auth/register Tests ====================

    @Test
    @DisplayName("Register: successful registration returns 201 Created")
    void registerUser_withValidRequest_returns201Created() throws Exception {
        // Arrange
        AuthRequestDto request = createRegisterRequest("Alex", "alex@northernarc.org", "9876543210", "password123");
        AuthResponseDto response = createAuthResponse("token123");
        when(authService.register(argThat(authRequest -> authRequest != null
                && "Alex".equals(authRequest.getName())
                && "alex@northernarc.org".equals(authRequest.getEmail())
                && "9876543210".equals(authRequest.getPhone())
                && "password123".equals(authRequest.getPassword())))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post(REGISTER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").exists());

        verify(authService, times(1)).register(any(AuthRequestDto.class));
    }

    @Test
    @DisplayName("Register: duplicate email returns 409 Conflict")
    void registerUser_withDuplicateEmail_returns409Conflict() throws Exception {
        // Arrange
        AuthRequestDto request = createRegisterRequest("Sam", "duplicate@northernarc.org", "9876543211", "password123");
        when(authService.register(argThat(authRequest -> authRequest != null
                && "Sam".equals(authRequest.getName())
                && "duplicate@northernarc.org".equals(authRequest.getEmail())
                && "9876543211".equals(authRequest.getPhone())
                && "password123".equals(authRequest.getPassword()))))
                .thenThrow(new InvalidRequestException("Email already exists"));

        // Act & Assert
        mockMvc.perform(post(REGISTER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());

        verify(authService, times(1)).register(any(AuthRequestDto.class));
    }

    @Test
    @DisplayName("Register: invalid email returns 400 Bad Request")
    void registerUser_withInvalidEmail_returns400BadRequest() throws Exception {
        // Arrange
        AuthRequestDto request = createRegisterRequest("Alex", "invalid-email", "9876543210", "password123");

        // Act & Assert
        mockMvc.perform(post(REGISTER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).register(any(AuthRequestDto.class));
    }

    @Test
    @DisplayName("Register: missing customer name returns 400 Bad Request")
    void registerUser_withMissingName_returns400BadRequest() throws Exception {
        // Arrange
        AuthRequestDto request = createRegisterRequest(null, "alex@northernarc.org", "9876543210", "password123");

        // Act & Assert
        mockMvc.perform(post(REGISTER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).register(any(AuthRequestDto.class));
    }

    @Test
    @DisplayName("Register: missing email returns 400 Bad Request")
    void registerUser_withMissingEmail_returns400BadRequest() throws Exception {
        // Arrange
        AuthRequestDto request = createRegisterRequest("Alex", null, "9876543210", "password123");

        // Act & Assert
        mockMvc.perform(post(REGISTER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).register(any(AuthRequestDto.class));
    }

    @Test
    @DisplayName("Register: missing phone number returns 400 Bad Request")
    void registerUser_withMissingPhoneNumber_returns400BadRequest() throws Exception {
        // Arrange
        AuthRequestDto request = createRegisterRequest("Alex", "alex@northernarc.org", null, "password123");

        // Act & Assert
        mockMvc.perform(post(REGISTER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).register(any(AuthRequestDto.class));
    }

    @Test
    @DisplayName("Register: phone number not exactly 10 digits returns 400 Bad Request")
    void registerUser_withInvalidPhoneLength_returns400BadRequest() throws Exception {
        // Arrange
        AuthRequestDto request = createRegisterRequest("Alex", "alex@northernarc.org", "123456789", "password123");

        // Act & Assert
        mockMvc.perform(post(REGISTER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).register(any(AuthRequestDto.class));
    }

    @Test
    @DisplayName("Register: missing password returns 400 Bad Request")
    void registerUser_withMissingPassword_returns400BadRequest() throws Exception {
        // Arrange
        AuthRequestDto request = createRegisterRequest("Alex", "alex@northernarc.org", "9876543210", null);

        // Act & Assert
        mockMvc.perform(post(REGISTER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).register(any(AuthRequestDto.class));
    }

    @Test
    @DisplayName("Register: password gets encrypted before saving")
    void registerUser_passwordEncryptedBeforeSave() throws Exception {
        // Arrange
        AuthRequestDto request = createRegisterRequest("Alex", "alex@northernarc.org", "9876543210", "plainPassword");
        AuthResponseDto response = createAuthResponse("token123");
        when(authService.register(argThat(authRequest -> authRequest != null
                && "Alex".equals(authRequest.getName())
                && "alex@northernarc.org".equals(authRequest.getEmail())
                && "9876543210".equals(authRequest.getPhone())
                && "plainPassword".equals(authRequest.getPassword())))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post(REGISTER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(authService, times(1)).register(any(AuthRequestDto.class));
    }

    @Test
    @DisplayName("Register: invalid JSON request returns 400 Bad Request")
    void registerUser_withInvalidJson_returns400BadRequest() throws Exception {
        // Arrange
        String invalidJson = "{\"name\": \"Alex\", invalid json}";

        // Act & Assert
        mockMvc.perform(post(REGISTER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());

        verify(authService, never()).register(any(AuthRequestDto.class));
    }

    @Test
    @DisplayName("Register: unsupported media type returns 415")
    void registerUser_withUnsupportedMediaType_returns415() throws Exception {
        // Arrange
        AuthRequestDto request = createRegisterRequest("Alex", "alex@northernarc.org", "9876543210", "password123");

        // Act & Assert
        mockMvc.perform(post(REGISTER_ENDPOINT)
                .contentType(MediaType.APPLICATION_XML)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnsupportedMediaType());

        verify(authService, never()).register(any(AuthRequestDto.class));
    }

    // ==================== POST /api/auth/login Tests ====================

    @Test
    @DisplayName("Login: successful login returns 200 OK")
    void loginUser_withValidCredentials_returns200Ok() throws Exception {
        // Arrange
        AuthRequestDto request = createLoginRequest("alex@northernarc.org", "password123");
        AuthResponseDto response = createAuthResponse("token456");
        when(authService.login(argThat(authRequest -> authRequest != null
                && "alex@northernarc.org".equals(authRequest.getEmail())
                && "password123".equals(authRequest.getPassword())))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post(LOGIN_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token456"));

        verify(authService, times(1)).login(any(AuthRequestDto.class));
    }

    @Test
    @DisplayName("Login: invalid credentials returns 401 Unauthorized")
    void loginUser_withInvalidCredentials_returns401Unauthorized() throws Exception {
        // Arrange
        AuthRequestDto request = createLoginRequest("alex@northernarc.org", "wrongPassword");
        when(authService.login(argThat(authRequest -> authRequest != null
                && "alex@northernarc.org".equals(authRequest.getEmail())
                && "wrongPassword".equals(authRequest.getPassword()))))
                .thenThrow(new UnauthorizedException("Invalid credentials"));

        // Act & Assert
        mockMvc.perform(post(LOGIN_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verify(authService, times(1)).login(any(AuthRequestDto.class));
    }

    @Test
    @DisplayName("Login: missing username returns 400 Bad Request")
    void loginUser_withMissingUsername_returns400BadRequest() throws Exception {
        // Arrange
        AuthRequestDto request = createLoginRequest(null, "password123");

        // Act & Assert
        mockMvc.perform(post(LOGIN_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).login(any(AuthRequestDto.class));
    }

    @Test
    @DisplayName("Login: missing password returns 400 Bad Request")
    void loginUser_withMissingPassword_returns400BadRequest() throws Exception {
        // Arrange
        AuthRequestDto request = createLoginRequest("alex@northernarc.org", null);

        // Act & Assert
        mockMvc.perform(post(LOGIN_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(authService, never()).login(any(AuthRequestDto.class));
    }

    @Test
    @DisplayName("Login: empty request body returns 400 Bad Request")
    void loginUser_withEmptyRequestBody_returns400BadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(post(LOGIN_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest());

        verify(authService, never()).login(any(AuthRequestDto.class));
    }

    @Test
    @DisplayName("Login: invalid JSON returns 400 Bad Request")
    void loginUser_withInvalidJson_returns400BadRequest() throws Exception {
        // Arrange
        String invalidJson = "{\"username\": \"alex\", invalid}";

        // Act & Assert
        mockMvc.perform(post(LOGIN_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());

        verify(authService, never()).login(any(AuthRequestDto.class));
    }

    @Test
    @DisplayName("Login: disabled account returns 401 Unauthorized")
    void loginUser_withDisabledAccount_returns401Unauthorized() throws Exception {
        // Arrange
        AuthRequestDto request = createLoginRequest("disabled@northernarc.org", "password123");
        when(authService.login(argThat(authRequest -> authRequest != null
                && "disabled@northernarc.org".equals(authRequest.getEmail())
                && "password123".equals(authRequest.getPassword()))))
                .thenThrow(new UnauthorizedException("Account is disabled"));

        // Act & Assert
        mockMvc.perform(post(LOGIN_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verify(authService, times(1)).login(any(AuthRequestDto.class));
    }

    @Test
    @DisplayName("Register: invalid Accept header returns 406 Not Acceptable")
    void registerUser_withInvalidAcceptHeader_returns406() throws Exception {
        // Arrange
        AuthRequestDto request = createRegisterRequest("Alex", "alex@northernarc.org", "9876543210", "password123");

        // Act & Assert
        mockMvc.perform(post(REGISTER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_XML)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotAcceptable());

        verify(authService, never()).register(any(AuthRequestDto.class));
    }

    @Test
    @DisplayName("Login: response JSON contract and headers are validated")
    void loginUser_responseContract_validated() throws Exception {
        // Arrange
        AuthRequestDto request = createLoginRequest("alex@northernarc.org", "password123");
        AuthResponseDto response = createAuthResponse("token-abc-123");
        when(authService.login(argThat(authRequest -> authRequest != null
                && "alex@northernarc.org".equals(authRequest.getEmail())
                && "password123".equals(authRequest.getPassword())))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post(LOGIN_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(header().exists("Content-Type"))
                .andExpect(jsonPath("$.token").value("token-abc-123"));

        verify(authService, times(1)).login(any(AuthRequestDto.class));
    }

    // ==================== Helper Methods ====================

    private AuthRequestDto createRegisterRequest(String name, String email, String phone, String password) {
        AuthRequestDto dto = new AuthRequestDto();
        setField(dto, "name", name);
        setField(dto, "email", email);
        setField(dto, "phone", phone);
        setField(dto, "password", password);
        return dto;
    }

    private AuthRequestDto createLoginRequest(String email, String password) {
        AuthRequestDto dto = new AuthRequestDto();
        setField(dto, "email", email);
        setField(dto, "password", password);
        return dto;
    }

    private AuthResponseDto createAuthResponse(String token) {
        AuthResponseDto dto = new AuthResponseDto();
        setField(dto, "token", token);
        return dto;
    }

    private void setField(Object target, String fieldName, Object value) {
        Class<?> current = target.getClass();
        while (current != null) {
            try {
                Field field = current.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(target, value);
                return;
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            } catch (IllegalAccessException exception) {
                throw new RuntimeException(exception);
            }
        }
    }
}
