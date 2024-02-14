package com.preOrderService.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(OrderServiceException.class)
    public ResponseEntity<ErrorResponse> handle(OrderServiceException ex) {
        final ErrorCode errorCode = ex.getErrorCode();
        return new ResponseEntity<>(new ErrorResponse(errorCode, errorCode.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @Data
    public class ErrorResponse{
        private final ErrorCode errorCode;
        private final String message;
    }
}
