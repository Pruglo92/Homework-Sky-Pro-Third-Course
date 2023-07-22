package ru.hogwarts.school.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StudentResponseDto(
        @NotNull
        @JsonProperty("id")
        Long id,
        @NotBlank
        @JsonProperty("name")
        String name,
        @NotNull
        @JsonProperty("age")
        Integer age
) {
}
