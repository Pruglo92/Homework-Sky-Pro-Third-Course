package ru.hogwarts.school.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hogwarts.school.exceptions.EmptyStringException;
import ru.hogwarts.school.exceptions.FacultyAlreadyExistsException;
import ru.hogwarts.school.exceptions.FacultyNotFoundException;
import ru.hogwarts.school.exceptions.InvalidFacultySearchException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.List;
import java.util.Random;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class FacultyService {

    private final FacultyRepository facultyRepository;

    public Faculty addFaculty(final Faculty faculty) {
        log.info("Was invoked method for : {}", getMethodName());
        validateString(faculty.getName());
        log.debug("Faculty name verification passed");
        validateString(faculty.getColor());
        log.debug("Faculty color verification passed");

        if (facultyRepository.existsFacultyByNameAndColor(StringUtils.capitalize(faculty.getName()),
                StringUtils.capitalize(faculty.getColor()))) {
            log.warn("Faculty with the name \"{}\" already exists", faculty.getName());
            throw new FacultyAlreadyExistsException("Такой факультет уже существует");
        }

        faculty.setId(faculty.getId());
        faculty.setColor(StringUtils.capitalize(faculty.getColor()));
        faculty.setName(StringUtils.capitalize(faculty.getName()));
        log.debug("Saving faculty : {}", faculty.getName());

        try {
            return facultyRepository.save(faculty);
        } catch (Exception e) {
            log.error("Failed to save faculty", e);
            throw e;
        }
    }

    public Faculty getFacultyById(final Long id) {
        log.info("Was invoked method for : {}", getMethodName());
        log.debug("Getting faculty by ID : {}", id);

        return facultyRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Faculty not found with ID : {}", id);
                    return new FacultyNotFoundException("Отсутствует факультет по данному ИД");
                });

    }

    public Faculty changeFaculty(final Faculty faculty) {
        log.info("Was invoked method for : {}", getMethodName());
        validateString(faculty.getName());
        log.debug("Faculty name verification passed");
        validateString(faculty.getColor());
        log.debug("Faculty color verification passed");

        if (!facultyRepository.existsById(faculty.getId())) {
            log.error("Faculty not found with ID : {}", faculty.getId());
            throw new FacultyNotFoundException("Факультет не найден");
        }

        faculty.setColor(StringUtils.capitalize(faculty.getColor()));
        faculty.setName(StringUtils.capitalize(faculty.getName()));
        log.debug("Changing faculty with ID : {}", faculty.getId());

        try {
            return facultyRepository.save(faculty);
        } catch (Exception e) {
            log.error("Failed to changing faculty", e);
            throw e;
        }
    }

    public void removeFacultyById(final Long id) {
        log.info("Was invoked method for : {}", getMethodName());
        if (!facultyRepository.existsById(id)) {
            log.error("Faculty not found with ID : {}", id);
            throw new FacultyNotFoundException("Отсутствует факультет по данному ИД");
        }
        log.debug("Removing faculty with ID : {}", id);
        try {
            facultyRepository.deleteById(id);
        } catch (Exception e) {
            log.error("Failed to removing faculty", e);
            throw e;
        }
    }

    public List<Faculty> getFacultiesByColor(final String color) {
        log.info("Was invoked method for : {}", getMethodName());
        validateString(color);
        log.debug("Faculty color verification passed");

        return facultyRepository.findAllByColor(color)
                .orElseThrow(() -> {
                    log.error("Faculty not found with color : {}", color);
                    return new FacultyNotFoundException("Факультеты с данным цветом не найдены");
                });
    }

    public Faculty getFacultyByColorOrName(final String string) {
        log.info("Was invoked method for : {}", getMethodName());
        validateString(string);
        log.debug("Faculty color or name verification passed");

        log.debug("Search departments by name or color");
        var faculties = facultyRepository.findWithParam(string, string);

        if (faculties.isEmpty()) {
            log.error("Faculty not found with color or name : {}", string);
            throw new FacultyNotFoundException("Факультеты с данным цветом или именем не найдены");
        }
        log.debug("Getting a random faculty from all found by name or color");
        return faculties.get(new Random().nextInt(faculties.size()));
    }

    public Faculty getFacultyByStudentIdOrName(final Long id, final String name) {
        log.info("Was invoked method for : {}", getMethodName());

        if (id != null && name != null) {
            log.debug("Search for faculty by id or name");

            return facultyRepository.findByStudentsId(id)
                    .orElse(facultyRepository.findByStudentsName(name)
                            .orElseThrow(() -> {
                                log.error("Faculty not found with student ID : {} or student name : {}", id, name);
                                return new FacultyNotFoundException("Отсутствует факультет по данному ИД студента или имени студента");
                            }));
        } else if (id != null) {
            log.debug("Search for faculty by id");

            return facultyRepository.findByStudentsId(id)
                    .orElseThrow(() -> {
                        log.error("Faculty not found with student ID : {}", id);
                        return new FacultyNotFoundException("Отсутствует факультет по данному ИД студента");
                    });
        } else if (name != null) {
            validateString(name);
            log.debug("Student name verification passed");
            log.debug("Search for faculty by name");

            return facultyRepository.findByStudentsName(name)
                    .orElseThrow(() -> {
                        log.error("Faculty not found with student name : {}", name);
                        return new FacultyNotFoundException("Отсутствует факультет по данному имени студента");
                    });
        }
        log.error("Invalid faculty search: Neither student ID nor student name is specified");
        throw new InvalidFacultySearchException("Не указан ни ИД студента, ни имя студента");
    }

    private void validateString(String string) {
        log.info("Was invoked method for : {}", getMethodName());
        if (!StringUtils.isAlpha(string)) {
            log.error("Invalid request parameter");
            throw new EmptyStringException("Запрос может содержать только буквы");
        }
    }

    private static String getMethodName() {
        return convertCamelCaseToSpace(Thread.currentThread().getStackTrace()[2].getMethodName());
    }

    private static String convertCamelCaseToSpace(String camelCaseString) {
        return camelCaseString.replaceAll("(?<=.)([A-Z])", " $1").toLowerCase();
    }
}
