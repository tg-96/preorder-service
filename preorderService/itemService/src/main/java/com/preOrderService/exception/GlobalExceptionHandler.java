package com.preOrderService.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ItemServiceException.class)
    public ResponseEntity<ErrorResponse> handle(ItemServiceException ex) {
        final ErrorCode errorCode = ex.getErrorCode();
        return new ResponseEntity<>(new ErrorResponse(errorCode, errorCode.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @Data
    public class ErrorResponse{
        private final ErrorCode errorCode;
        private final String message;
    }
}
