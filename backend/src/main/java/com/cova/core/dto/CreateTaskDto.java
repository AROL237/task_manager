package com.cova.core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;



public record CreateTaskDto(

        @NotBlank(message = "missing title")
        @Size(min = 1)
        String title,
        String description,
        @NotNull
        boolean status
) {
}
