package ru.hogwarts.school.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FacultyResponseDto(
        @NotNull
        @JsonProperty("id")
        Long id,
        @NotBlank
        @JsonProperty("name")
        String name,
        @NotBlank
        @JsonProperty("color")
        String color
) {
}
