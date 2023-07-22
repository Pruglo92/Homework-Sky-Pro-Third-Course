package ru.hogwarts.school.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StudentRequestDto(
        @NotBlank
        @JsonProperty("name")
        String name,
        @NotNull
        @JsonProperty("age")
        Integer age
) {
}
