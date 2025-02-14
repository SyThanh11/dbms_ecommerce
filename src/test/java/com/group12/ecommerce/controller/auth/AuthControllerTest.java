package com.group12.ecommerce.controller.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.group12.ecommerce.base.exception.AppException;
import com.group12.ecommerce.base.exception.ErrorCode;
import com.group12.ecommerce.dto.request.auth.AuthRequest;
import com.group12.ecommerce.dto.request.auth.IntrospectRequest;
import com.group12.ecommerce.dto.request.auth.LogoutRequest;
import com.group12.ecommerce.dto.request.auth.RefreshRequest;
import com.group12.ecommerce.dto.request.user.UserCreationRequest;
import com.group12.ecommerce.dto.response.auth.AuthResponse;
import com.group12.ecommerce.dto.response.auth.IntrospectResponse;
import com.group12.ecommerce.dto.response.user.UserResponse;
import com.group12.ecommerce.entity.user.UserEntity;
import com.group12.ecommerce.repository.user.IUserRepository;
import com.group12.ecommerce.service.interfaceService.auth.IAuthService;
import com.group12.ecommerce.service.interfaceService.token.ITokenService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.Optional;

@SpringBootTest
@Slf4j
@AutoConfigureMockMvc
@FieldDefaults(level = AccessLevel.PRIVATE)
class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    PasswordEncoder passwordEncoder;

    @MockitoBean
    IAuthService authService;
    @MockitoBean
    ITokenService tokenService;
    @MockitoBean
    IUserRepository userRepository;

    // Sign Up
    UserCreationRequest request;
    UserResponse response;
    LocalDate dob;

    // Log In
    AuthRequest authRequest;
    AuthResponse authResponse;
    UserEntity user;

    // Introspect
    IntrospectRequest introspectRequest;
    IntrospectResponse introspectResponse;

    // refresh
    RefreshRequest refreshRequest;
    AuthResponse refreshResponse;

    // logout
    LogoutRequest logoutRequest;

    @BeforeEach
    void initData(){
        dob = LocalDate.of(2003,7, 11);

        request = UserCreationRequest.builder()
                .avatar("")
                .fullName("Nguyen Sy Thanh")
                .username("user")
                .password("123456")
                .dob(dob)
                .email("sythanhdev@gmail.com")
                .build();

        response = UserResponse.builder()
                .id("9d0f1a24-0818-4708-b176-7c731d0cf860")
                .avatar("")
                .fullName("Nguyen Sy Thanh")
                .username("user")
                .email("sythanhdev@gmail.com")
                .dob(dob)
                .build();

        authRequest = AuthRequest.builder()
                .username("user")
                .password("123456")
                .build();
        authResponse = AuthResponse.builder()
                .token("generated_token")
                .build();
        user = UserEntity.builder()
                .email("sythanhdev@gmail.com")
                .username("user")
                .password(passwordEncoder.encode("123456"))
                .build();

        introspectRequest = IntrospectRequest.builder()
                .token("token")
                .build();
        introspectResponse = IntrospectResponse.builder()
                .valid(true)
                .build();

        refreshRequest = RefreshRequest.builder()
                .token("token")
                .build();
        refreshResponse = AuthResponse.builder()
                .token("generated_token")
                .build();

        logoutRequest = LogoutRequest.builder()
                .token("token")
                .build();
    }

    @Test
    void signUp_validRequest_success() throws Exception {
        // GIVEN
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(request);

        Mockito.when(authService.signUp(ArgumentMatchers.any())).thenReturn(response);

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(content))
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andExpect(MockMvcResultMatchers.jsonPath("code").value(2000))
            .andExpect(MockMvcResultMatchers.jsonPath("data.id")
                    .value("9d0f1a24-0818-4708-b176-7c731d0cf860"))
            .andExpect(MockMvcResultMatchers.jsonPath("data.username")
                    .value("user"))
            .andExpect(MockMvcResultMatchers.jsonPath("data.fullName")
                    .value("Nguyen Sy Thanh"))
            .andExpect(MockMvcResultMatchers.jsonPath("data.email")
                    .value("sythanhdev@gmail.com"));
    }

    @Test
    void signUp_invalidRequest_error() throws Exception {
        // GIVEN
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(request);

        // Simulate the exception being thrown by the service
        Mockito.when(authService.signUp(ArgumentMatchers.any()))
                .thenThrow(new AppException(ErrorCode.USERNAME_EXISTED));

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isConflict()) // or another status you prefer for the exception
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1002)) // Example error code for integrity violation
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("Username is existed!"));
    }

    @Test
    void logIn_validRequest_success() throws Exception {
        // GIVEN
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(authRequest);

        Mockito.when(userRepository.findByUsername(ArgumentMatchers.anyString())).thenReturn(Optional.of(user));
        Mockito.when(tokenService.generateToken(ArgumentMatchers.any(UserEntity.class)))
                .thenReturn("generated_token");
        Mockito.when(authService.logIn(ArgumentMatchers.any(AuthRequest.class))).thenReturn(authResponse);

        // WHEN, THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isAccepted())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(2000))
                .andExpect(MockMvcResultMatchers.jsonPath("data.token")
                        .value("generated_token"));
    }

    @Test
    void logIn_usernameNotFound_error() throws Exception {
        // GIVEN
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(authRequest);

        Mockito.when(userRepository.findByUsername(ArgumentMatchers.anyString())).thenReturn(Optional.empty());
        Mockito.when(authService.logIn(ArgumentMatchers.any(AuthRequest.class))).thenThrow(
                new AppException(ErrorCode.USER_NOT_EXISTED)
        );

        // WHEN & THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1001))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("User is not existed!"));
    }

    @Test
    void logIn_passwordWrong_error() throws Exception {
        // GIVEN
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(authRequest);

        Mockito.when(userRepository.findByUsername(ArgumentMatchers.anyString())).thenReturn(Optional.of(user));
        Mockito.when(authService.logIn(ArgumentMatchers.any(AuthRequest.class))).thenThrow(
                new AppException(ErrorCode.PASSWORD_WRONG)
        );

        // WHEN & THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(1003))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("Password is wrong!"));
    }

    @Test
    void introspect_validRequest_success() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(introspectRequest);

        Mockito.when(authService.introspect(ArgumentMatchers.any(IntrospectRequest.class)))
                .thenReturn(introspectResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/introspect")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(2000))
                .andExpect(MockMvcResultMatchers.jsonPath("data.valid").value(introspectResponse.isValid()));
    }

    @Test
    void refreshToken_validRequest_success() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(refreshRequest);

        Mockito.when(tokenService.refreshToken(ArgumentMatchers.any(RefreshRequest.class)))
                .thenReturn(refreshResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(2000))
                .andExpect(MockMvcResultMatchers.jsonPath("data.token").value(refreshResponse.getToken()));
    }

    @Test
    void logOut_validRequest_success() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(logoutRequest);

        Mockito.doNothing().when(authService).logOut(ArgumentMatchers.any(LogoutRequest.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isAccepted())
                .andExpect(MockMvcResultMatchers.jsonPath("code").value(2000))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("Log out success!"));
    }
}
