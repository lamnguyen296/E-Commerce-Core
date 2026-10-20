package com.restapi.productservice.exception;

import com.restapi.productservice.dto.response.BaseResponse;
import com.restapi.productservice.dto.response.FieldViolation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    ResponseEntity<BaseResponse<Object>> handlingRuntimeException(Exception exception) {
        log.error("Unhandled Exception: ", exception);
        return ResponseEntity.badRequest().body(
                BaseResponse.ofFail(
                        ErrorCode.UNCATEGORIZED_EXCEPTION.getCode(),
                        ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage()
                )
        );
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<BaseResponse<Object>> handlingAppException(AppException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        return ResponseEntity
                .status(errorCode.getStatusCode())
                .body(BaseResponse.ofFail(errorCode.getCode(), errorCode.getMessage()));
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<BaseResponse<Object>> handlingValidation(MethodArgumentNotValidException exception) {
        List<FieldViolation> violations = new ArrayList<>();

        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            violations.add(FieldViolation.builder()
                    .field(fieldError.getField())
                    .message(fieldError.getDefaultMessage())
                    .build());
        }

        return ResponseEntity.badRequest().body(
                BaseResponse.ofFail(
                        ErrorCode.INVALID_KEY.getCode(),
                        "Validation error",
                        violations
                )
        );
    }
}
