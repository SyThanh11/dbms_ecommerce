package com.group12.ecommerce.service.interfaceService.token;

import com.group12.ecommerce.dto.request.auth.RefreshRequest;
import com.group12.ecommerce.dto.response.auth.AuthResponse;
import com.group12.ecommerce.entity.user.UserEntity;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;

import java.text.ParseException;

public interface ITokenService {
    String generateToken(UserEntity user);
    AuthResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException;
    String buildScope(UserEntity user);
    SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException;
}
