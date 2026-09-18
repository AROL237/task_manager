package com.cova.core.dto;


import java.time.LocalDateTime;


public record ApiResponse<T>(int code, String message, boolean success, LocalDateTime timestamp , T data) {
}
