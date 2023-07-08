package ru.hogwarts.school.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hogwarts.school.exceptions.EmptyStringException;
import ru.hogwarts.school.exceptions.FacultyAlreadyExistsException;
import ru.hogwarts.school.exceptions.FacultyNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@Transactional
@RequiredArgsConstructor
public class FacultyService {

    private final FacultyRepository facultyRepository;

    public Faculty addFaculty(final Faculty faculty) {
        validateString(faculty.getName());
        validateString(faculty.getColor());

        if (facultyRepository.existsFacultyByNameAndColor(StringUtils.capitalize(faculty.getName()),
                StringUtils.capitalize(faculty.getColor()))) {
            throw new FacultyAlreadyExistsException("Такой факультет уже существует");
        }

        faculty.setId(faculty.getId());
        faculty.setColor(StringUtils.capitalize(faculty.getColor()));
        faculty.setName(StringUtils.capitalize(faculty.getName()));
        return facultyRepository.save(faculty);
    }

    public Faculty getFacultyById(final Long id) {
        return facultyRepository.findById(id)
                .orElseThrow(() -> new FacultyNotFoundException("Отсутствует Факультет по данному ID"));

    }

    public Faculty changeFaculty(final Faculty faculty) {
        validateString(faculty.getName());
        validateString(faculty.getColor());

        if (!facultyRepository.existsById(faculty.getId())) {
            throw new FacultyNotFoundException("Факультет не найден");
        }

        faculty.setColor(StringUtils.capitalize(faculty.getColor()));
        faculty.setName(StringUtils.capitalize(faculty.getName()));

        return facultyRepository.save(faculty);
    }

    public void removeFacultyById(final Long id) {
        if (!facultyRepository.existsById(id)) {
            throw new FacultyNotFoundException("Отсутствует Факультет по данному ID");
        }
        facultyRepository.deleteById(id);
    }

    private void validateString(String string) {
        if (!StringUtils.isAlpha(string)) {
            throw new EmptyStringException("Запрос может содержать только буквы");
        }
    }

    public List<Faculty> getFacultiesByColor(final String color) {
        validateString(color);
        return facultyRepository.findAllByColor(color)
                .orElseThrow(() -> new FacultyNotFoundException("Факультеты с данным цветом не найдены"));
    }

    public Faculty getFacultyByColorOrName(final String string) {
        validateString(string);
        var faculties = facultyRepository.findWithParam(string, string);

        if (faculties.isEmpty()) {
            throw new FacultyNotFoundException("Факультеты с данным цветом или именем не найдены");
        }
        return faculties.get(new Random().nextInt(faculties.size()));
    }

    public Faculty getFacultyByStudentIdOrName(final Long id, final String name) {
        return Optional.ofNullable(id)
                .map(facultyRepository::findByStudentsId)
                .orElseGet(() -> Optional.ofNullable(name)
                        .map(facultyRepository::findByStudentsName)
                        .orElseThrow(() -> new FacultyNotFoundException("Отсутствует факультет по данному ID или имени")))
                .orElseThrow(() -> new FacultyNotFoundException("Отсутствует факультет по данному ID или имени"));
    }
}
