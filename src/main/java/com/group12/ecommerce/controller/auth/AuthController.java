package com.group12.ecommerce.controller.auth;

import com.group12.ecommerce.dto.request.auth.*;
import com.group12.ecommerce.dto.request.user.UserCreationRequest;
import com.group12.ecommerce.dto.response.api.ApiResponse;
import com.group12.ecommerce.dto.response.auth.AuthResponse;
import com.group12.ecommerce.dto.response.auth.IntrospectResponse;
import com.group12.ecommerce.dto.response.user.UserResponse;
import com.group12.ecommerce.service.interfaceService.auth.IAuthService;
import com.group12.ecommerce.service.interfaceService.token.ITokenService;
import com.nimbusds.jose.JOSEException;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@Slf4j
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Auth Management")
public class AuthController {
    @Autowired
    IAuthService authService;
    @Autowired
    ITokenService tokenService;

    @PostMapping("/signup")
    ResponseEntity<ApiResponse<UserResponse>> signUp(@RequestBody UserCreationRequest request){
        UserResponse userResponse = authService.signUp(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<UserResponse>builder()
                        .code(2000)
                        .message("Sign up success!")
                        .data(userResponse)
                        .build()
        );
    }

    @PostMapping("/login")
    ResponseEntity<ApiResponse<AuthResponse>> logIn(@RequestBody AuthRequest request){
        AuthResponse authResponse = authService.logIn(request);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(
                ApiResponse.<AuthResponse>builder()
                        .message("Log in success!")
                        .data(authResponse)
                        .build()
        );
    }

    @PostMapping("/introspect")
    ResponseEntity<ApiResponse<IntrospectResponse>> introspect(@RequestBody IntrospectRequest request) throws ParseException, JOSEException {
        IntrospectResponse introspectResponse = authService.introspect(request);

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<IntrospectResponse>builder()
                        .message("Token is valid!")
                        .data(introspectResponse)
                        .build()
        );
    }

    @PostMapping("/logout")
    ResponseEntity<ApiResponse<?>> logOut(@RequestBody LogoutRequest request) throws ParseException, JOSEException {
        authService.logOut(request);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(
                ApiResponse.builder()
                        .message("Log out success!")
                        .build()
        );
    }

    @PostMapping("/refresh")
    ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@RequestBody RefreshRequest request) throws ParseException, JOSEException {
        AuthResponse authResponse = tokenService.refreshToken(request);

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<AuthResponse>builder()
                        .message("Refresh Token success")
                        .data(authResponse)
                        .build()
        );
    }

    @PostMapping("/forgot-password")
    ResponseEntity<ApiResponse<?>> forgotPassword(@RequestBody ForgotPasswordRequest request){
        authService.forgotPassword(request);

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .message("Email send with new password success!")
                        .build()
        );
    }
}
