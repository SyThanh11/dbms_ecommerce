package com.group12.ecommerce.service.implementService.auth;

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
import com.group12.ecommerce.entity.role.RoleEntity;
import com.group12.ecommerce.entity.token.InvalidatedTokenEntity;
import com.group12.ecommerce.entity.user.UserEntity;
import com.group12.ecommerce.mapper.user.IUserMapper;
import com.group12.ecommerce.repository.role.IRoleRepository;
import com.group12.ecommerce.repository.token.IInvalidatedTokenRepository;
import com.group12.ecommerce.repository.user.IUserRepository;
import com.group12.ecommerce.service.interfaceService.auth.IAuthService;
import com.group12.ecommerce.service.interfaceService.email.IEmailService;
import com.group12.ecommerce.service.interfaceService.token.ITokenService;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.mail.MessagingException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.SecureRandom;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthService implements IAuthService {
    @Autowired
    IUserRepository userRepository;
    @Autowired
    IInvalidatedTokenRepository invalidatedTokenRepository;
    @Autowired
    IRoleRepository roleRepository;

    @Autowired
    IUserMapper userMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    ITokenService tokenService;
    @Autowired
    IEmailService emailService;

    @Override
    public UserResponse signUp(UserCreationRequest request) {
        try {
            UserEntity user = userMapper.toUserEntity(request);
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            RoleEntity role = roleRepository.findByName("USER")
                    .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

            user.setRoles(new HashSet<>(Collections.singleton(role)));

            return userMapper.toUserResponse(userRepository.save(user));
        } catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.USERNAME_EXISTED);
        }
    }

    @Override
    public AuthResponse logIn(AuthRequest request) {
        UserEntity user = userRepository
                .findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if(!authenticated){
            throw new AppException(ErrorCode.PASSWORD_WRONG);
        }

        String token = tokenService.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .build();
    }

    @Override
    public IntrospectResponse introspect(IntrospectRequest request) throws ParseException, JOSEException {
        String token = request.getToken();
        boolean isValid = true;

        try {
            tokenService.verifyToken(token, false);
        } catch (AppException e){
            isValid = false;
        }

        return IntrospectResponse.builder()
                .valid(isValid)
                .build();
    }

    @Override
    public void logOut(LogoutRequest request) throws ParseException, JOSEException {
        try {
            SignedJWT signedJWT = tokenService.verifyToken(request.getToken(), true);

            String jit = signedJWT.getJWTClaimsSet().getJWTID();
            Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

            InvalidatedTokenEntity invalidatedToken = InvalidatedTokenEntity.builder()
                    .id(jit)
                    .expiryTime(expiryTime)
                    .build();

            invalidatedTokenRepository.save(invalidatedToken);
        } catch (AppException e) {
            throw new AppException(ErrorCode.TOKEN_INVALID);
        }
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request) {
        UserEntity user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        String newPassword = generateRandomPassword(8);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        Map<String, String> placeholders = Map.of(
                "name", user.getFullName(),
                "newPassword", newPassword
        );
        log.info(user.getUsername());

        // Gửi email bất đồng bộ
        CompletableFuture.runAsync(() -> {
            try {
                emailService.sendEmailFromTemplate(
                        "no-reply@ecommerce.com",
                        user.getEmail(),
                        "Reset Password",
                        "templates/htmlForgotPasswordTemplate.html",
                        placeholders
                );
                log.info("✅ Password reset email sent to {}", user.getEmail());
            } catch (MessagingException | IOException e) {
                log.error("❌ Failed to send reset password email", e);
            }
        });

        log.info("🔄 Processing password reset for {}", user.getUsername());
    }


    @Override
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null){
            return authentication.getName();
        }

        throw new AppException(ErrorCode.UNAUTHENTICATED);
    }

    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

}
