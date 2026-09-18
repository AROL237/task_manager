package com.cova.core.exception;

import com.cova.core.Constants;
import com.cova.core.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleException(RuntimeException exception) {
        log.error("{}", exception.getMessage(), exception);
        String message = exception.getMessage();
        ApiResponse<Void> out = new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), message, false, LocalDateTime.now(), null);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(out);
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<Void>> handleException(ApiException exception) {
        log.error("{}", exception.getMessage(), exception);
        String message = exception.getMessage();
        ApiResponse<Void> out = new ApiResponse<>(exception.getStatus().value(), message, false, LocalDateTime.now(), null);
        return ResponseEntity.status(exception.getStatus()).body(out);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleException(MethodArgumentNotValidException exception) {
        log.error("{}", exception.getMessage(), exception);

        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult()

                .getFieldErrors()
                .forEach(error ->

                        errors.put(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );
        String msg = String.join(" - ", Constants.FAILED, "input validation error");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), msg, false, LocalDateTime.now(), errors));
    }

//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    @ExceptionHandler({NoSuchElementException.class, UsernameNotFoundException.class})
//    public ResponseEntity<ApiResponse<Void>> handleException(Exception exception) {
//        log.error("{}", exception.getMessage(), exception);
//        String msg = exception.getMessage();
//        ApiResponse<Void> out = new ApiResponse<>(HttpStatus.NOT_FOUND.value(), msg, false, LocalDateTime.now(), null);
//        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(out);
//    }



//    @ExceptionHandler(IllegalArgumentException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ResponseEntity<ApiResponse<Void>> handleException(IllegalArgumentException exception) {
//        log.error("{}", exception.getMessage(), exception);
//        String msg = exception.getMessage();
//        ApiResponse<Void> out = new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), msg, false, LocalDateTime.now(), null);
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(out);
//    }

}
