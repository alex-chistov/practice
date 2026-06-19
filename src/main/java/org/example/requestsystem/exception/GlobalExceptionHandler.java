package org.example.requestsystem.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.example.requestsystem.dto.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> handleResponseStatus(ResponseStatusException ex, HttpServletRequest request) {
        int status = ex.getStatusCode().value();
        String message = ex.getReason() == null ? "Ошибка запроса" : ex.getReason();
        return ResponseEntity.status(status).body(new ApiError(status, message, request.getRequestURI()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));

        return ResponseEntity.badRequest().body(new ApiError(HttpStatus.BAD_REQUEST.value(), message, request.getRequestURI()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        return ResponseEntity.badRequest().body(new ApiError(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), request.getRequestURI()));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiError> handleMaxUploadSize(HttpServletRequest request) {
        return ResponseEntity.badRequest().body(new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "Превышен максимальный размер загружаемых файлов",
                request.getRequestURI()
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAny(Exception ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Внутренняя ошибка сервера",
                request.getRequestURI()
        ));
    }
}
