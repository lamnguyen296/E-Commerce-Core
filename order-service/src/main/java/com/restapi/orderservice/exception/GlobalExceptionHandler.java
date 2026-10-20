package com.restapi.orderservice.exception;

import com.restapi.orderservice.dto.response.BaseResponse;
import com.restapi.orderservice.dto.response.FieldViolation;
import jakarta.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String MIN_ATTRIBUTE = "min";

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<BaseResponse<Object>> handlingRuntimeException(RuntimeException exception){
        log.error("RuntimeException: ", exception);
        return ResponseEntity.badRequest().body(
                BaseResponse.ofFail(
                        ErrorCode.UNCATEGORIZED_EXCEPTION.getCode(),
                        exception.getMessage() != null ? exception.getMessage() : ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage()
                )
        );
    }

    @ExceptionHandler(value = {
            org.springframework.web.context.request.async.AsyncRequestNotUsableException.class,
            org.apache.catalina.connector.ClientAbortException.class
    })
    public void handlingClientAbort(Exception exception) {
        log.warn("Client disconnected before response was flushed: {}", exception.getMessage());
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<BaseResponse<Object>> handlingException(Exception exception){
        log.error("Unhandled Exception: ", exception);
        return ResponseEntity.badRequest().body(
                BaseResponse.ofFail(
                        ErrorCode.UNCATEGORIZED_EXCEPTION.getCode(),
                        exception.getMessage() != null ? exception.getMessage() : ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage()
                )
        );
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<BaseResponse<Object>> handlingAppException(AppException exception){
        ErrorCode errorCode = exception.getErrorCode();
        return ResponseEntity
                .status(errorCode.getStatusCode())
                .body(BaseResponse.ofFail(errorCode.getCode(), errorCode.getMessage()));
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<BaseResponse<Object>> handlingAccessDeniedException(AccessDeniedException exception){
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
        return ResponseEntity.status(errorCode.getStatusCode()).body(
                BaseResponse.ofFail(errorCode.getCode(), errorCode.getMessage())
        );
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<BaseResponse<Object>> handlingValidation(MethodArgumentNotValidException exception){
        List<FieldViolation> violations = new ArrayList<>();
        String enumKey = null;

        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            enumKey = fieldError.getDefaultMessage();
            String errorMsg = enumKey;
            try {
                ErrorCode ec = ErrorCode.valueOf(enumKey);
                errorMsg = ec.getMessage();
            } catch (Exception ignored) {
            }
            violations.add(FieldViolation.builder()
                    .field(fieldError.getField())
                    .message(errorMsg)
                    .build());
        }

        ErrorCode errorCode = ErrorCode.INVALID_KEY;
        Map<String, Object> attributes = null;
        try {
            if (enumKey != null) {
                errorCode = ErrorCode.valueOf(enumKey);
                var constraintViolation = exception.getBindingResult()
                        .getAllErrors().getFirst().unwrap(ConstraintViolation.class);
                attributes = constraintViolation.getConstraintDescriptor().getAttributes();
            }
        } catch (Exception ignored) {
        }

        String message = Objects.nonNull(attributes) ?
                mapAttribute(errorCode.getMessage(), attributes)
                : errorCode.getMessage();

        return ResponseEntity.badRequest().body(
                BaseResponse.ofFail(errorCode.getCode(), message, violations)
        );
    }

    private String mapAttribute(String message, Map<String, Object> attributes){
        String minValue = String.valueOf(attributes.get(MIN_ATTRIBUTE));
        return message.replace("{" + MIN_ATTRIBUTE + "}", minValue);
    }
}