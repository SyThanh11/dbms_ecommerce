package com.group12.ecommerce.service.interfaceService.auth;

import com.group12.ecommerce.base.exception.AppException;
import com.group12.ecommerce.base.exception.ErrorCode;
import com.group12.ecommerce.dto.request.auth.AuthRequest;
import com.group12.ecommerce.dto.request.auth.ForgotPasswordRequest;
import com.group12.ecommerce.dto.request.auth.IntrospectRequest;
import com.group12.ecommerce.dto.request.auth.LogoutRequest;
import com.group12.ecommerce.dto.request.user.UserCreationRequest;
import com.group12.ecommerce.dto.response.auth.AuthResponse;
import com.group12.ecommerce.dto.response.auth.IntrospectResponse;
import com.group12.ecommerce.dto.response.user.UserResponse;
import com.nimbusds.jose.JOSEException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.text.ParseException;

public interface IAuthService {
    UserResponse signUp(UserCreationRequest request);
    AuthResponse logIn(AuthRequest request);
    IntrospectResponse introspect(IntrospectRequest request) throws ParseException, JOSEException;
    void logOut(LogoutRequest request) throws ParseException, JOSEException;
    void forgotPassword(ForgotPasswordRequest request);

    String getCurrentUsername();
}
