package ru.hogwarts.school.mapper;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.hogwarts.school.dto.FacultyRequestDto;
import ru.hogwarts.school.dto.FacultyResponseDto;
import ru.hogwarts.school.model.Faculty;

import java.util.List;

@Mapper(imports = StringUtils.class)
public interface FacultyMapper {

    @Mapping(target = "name", expression = "java(StringUtils.capitalize(dto.name()))")
    Faculty toEntity(FacultyRequestDto dto);

    FacultyResponseDto toDto(Faculty entity);

    List<FacultyResponseDto> toDto(List<Faculty> entity);
}
