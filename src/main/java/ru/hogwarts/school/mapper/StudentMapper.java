package ru.hogwarts.school.mapper;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.hogwarts.school.dto.StudentRequestDto;
import ru.hogwarts.school.dto.StudentResponseDto;
import ru.hogwarts.school.model.Student;

import java.util.List;

@Mapper(imports = StringUtils.class)
public interface StudentMapper {

    @Mapping(target = "name", expression = "java(StringUtils.capitalize(dto.name()))")
    Student toEntity(StudentRequestDto dto);

    StudentResponseDto toDto(Student entity);

    List<StudentResponseDto> toDto(List<Student> entity);
}
