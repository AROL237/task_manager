package com.cova.core.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
public class ApiException extends RuntimeException {
    private HttpStatus status;
    private String message;



    public ApiException(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }



}
