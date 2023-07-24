package ru.hogwarts.school.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record FacultyRequestDto(
        @NotBlank
        @JsonProperty("name")
        String name,
        @NotBlank
        @JsonProperty("color")
        String color
) {
}
