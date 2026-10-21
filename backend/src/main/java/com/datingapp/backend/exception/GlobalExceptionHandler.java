package com.datingapp.backend.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.util.Map;
import java.util.LinkedHashMap;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler{

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex, HttpServletRequest request){

        return buildResponse(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", ex.getMessage(), request);
    }

    @ExceptionHandler(UniqueConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleUniqueConstraintViolation(UniqueConstraintViolationException ex, HttpServletRequest request){

        return buildResponse(HttpStatus.CONFLICT, "DUPLICATE_RESOURCE", ex.getMessage(), request);
    }

    @ExceptionHandler(ContentPolicyViolationException.class)
    public ResponseEntity<ErrorResponse> handleContentPolicyViolation(ContentPolicyViolationException ex, HttpServletRequest request){

        return buildResponse( HttpStatus.UNPROCESSABLE_ENTITY, "CONTENT_POLICY_VIOLATION", ex.getMessage(), request);
    }

    @ExceptionHandler(TooManyLoginAttemptsException.class)
    public ResponseEntity<ErrorResponse> handleTooManyLoginAttempts(TooManyLoginAttemptsException ex, HttpServletRequest request){

        return buildResponse(HttpStatus.TOO_MANY_REQUESTS, "TOO_MANY_LOGIN_ATTEMPTS", ex.getMessage(), request);
    }

    @ExceptionHandler(InvalidOtpException.class)
    public ResponseEntity<ErrorResponse> handleInvalidOtp(InvalidOtpException ex, HttpServletRequest request){

        return buildResponse(HttpStatus.UNAUTHORIZED, "INVALID_OTP", ex.getMessage(), request);
    }

    @ExceptionHandler(InvalidTokenTypeException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTokenType(InvalidTokenTypeException ex, HttpServletRequest request){

        return buildResponse(HttpStatus.BAD_REQUEST,"INVALID_TOKEN_TYPE", ex.getMessage(), request);
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPassword(InvalidPasswordException ex, HttpServletRequest request){

        return buildResponse(HttpStatus.BAD_REQUEST, "INVALID_PASSWORD", ex.getMessage(), request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request){

        return buildResponse(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", "The request is invalid.",request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request){

        return buildResponse(HttpStatus.FORBIDDEN, "ACCESS_DENIED", "You do not have permission to perform this action.", request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request){

        log.error("Unexpected exception at {}", request.getRequestURI(), ex);

        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "An unexpected error occurred.", request);
    }

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String code, String message, HttpServletRequest request){

        ErrorResponse error = new ErrorResponse(LocalDateTime.now(), status.value(), code, message, request.getRequestURI(), null);

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(ConflictException ex, HttpServletRequest request){

        ErrorResponse error = new ErrorResponse(LocalDateTime.now(), HttpStatus.CONFLICT.value(), "CONFLICT", ex.getMessage(), request.getRequestURI(), null);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(TooManyRequestsException.class)
    public ResponseEntity<ErrorResponse> handleTooManyRequests(TooManyRequestsException ex, HttpServletRequest request){

        ErrorResponse error = new ErrorResponse(LocalDateTime.now(), HttpStatus.TOO_MANY_REQUESTS.value(), "TOO_MANY_REQUESTS", ex.getMessage(), request.getRequestURI(), null);

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(error);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request){

        return buildResponse(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", ex.getMessage(), request);
    }

    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<ErrorResponse> handleFileStorageException(FileStorageException ex, HttpServletRequest request){

        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "FILE_STORAGE_ERROR", "A file storage error occurred.", request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request){

        Map<String, String> fieldErrors = new LinkedHashMap<>();

        for(FieldError error : ex.getBindingResult().getFieldErrors()){
            fieldErrors.putIfAbsent(error.getField(), error.getDefaultMessage() != null ? error.getDefaultMessage() : "Invalid value.");
        }

        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), "VALIDATION_FAILED",
                "One or more fields are invalid.", request.getRequestURI(), fieldErrors);

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MissingServletRequestParameterException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErrorResponse> handleBadRequest(Exception ex, HttpServletRequest request){

        return buildResponse(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", "The request contains invalid or missing data.", request);
    }
}