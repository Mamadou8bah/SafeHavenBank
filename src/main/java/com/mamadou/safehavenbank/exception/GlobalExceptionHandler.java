package com.mamadou.safehavenbank.exception;

import com.mamadou.safehavenbank.dto.APIResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyFoundException.class)
    public ResponseEntity<APIResponse<Object>> handleUserAlreadyFoundException(UserAlreadyFoundException ex, WebRequest request) {
        APIResponse<Object> response =APIResponse.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnverifiedUserException.class)
    public ResponseEntity<APIResponse<Object>> handleUserAlreadyFoundException(UnverifiedUserException ex, WebRequest request) {
        APIResponse<Object> response =APIResponse.error(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
