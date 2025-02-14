package com.group12.ecommerce.base.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),

    // Error for AppException
    USER_NOT_EXISTED(1001, "User is not existed!", HttpStatus.BAD_REQUEST),
    USERNAME_EXISTED(1002, "Username is existed!", HttpStatus.CONFLICT),
    PASSWORD_WRONG(1003, "Password is wrong!", HttpStatus.BAD_REQUEST),
    TOKEN_INVALID(1004, "Token is invalid!", HttpStatus.BAD_REQUEST),
    NAME_PERMISSION_EXISTED(1005, "Name's permission is existed!", HttpStatus.BAD_REQUEST),
    NAME_PERMISSION_NOT_EXISTED(1006, "Name's permission is not existed!", HttpStatus.BAD_REQUEST),
    NAME_ROLE_EXISTED(1007, "Name's role is existed!", HttpStatus.BAD_REQUEST),
    NAME_ROLE_NOT_EXISTED(1008, "Name's role is not existed!", HttpStatus.BAD_REQUEST),
    NAME_CATEGORY_EXISTED(1009, "Name's category is existed!", HttpStatus.BAD_REQUEST),
    NAME_CATEGORY_NOT_EXISTED(1010, "Name's category is not existed!", HttpStatus.BAD_REQUEST),
    NAME_PRODUCT_EXISTED(1011, "Name's product is existed!", HttpStatus.BAD_REQUEST),
    NAME_PRODUCT_NOT_EXISTED(1012, "Name's product is not existed!", HttpStatus.BAD_REQUEST),
    ORDER_NOT_EXISTED(1013, "Order is not existed!", HttpStatus.BAD_REQUEST),

    UNAUTHENTICATED(3001, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    TOKEN_NOT_CREATED(3002, "Token is not created!", HttpStatus.BAD_REQUEST),

    EMAIL_SEND_FAILED(4001, "Email send failed!", HttpStatus.BAD_REQUEST);

    int code;
    String message;
    HttpStatusCode httpStatusCode;
}
