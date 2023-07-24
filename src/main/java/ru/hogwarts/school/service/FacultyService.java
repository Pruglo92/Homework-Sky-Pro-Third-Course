package ru.hogwarts.school.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hogwarts.school.dto.FacultyRequestDto;
import ru.hogwarts.school.dto.FacultyResponseDto;
import ru.hogwarts.school.exceptions.EmptyStringException;
import ru.hogwarts.school.exceptions.FacultyAlreadyExistsException;
import ru.hogwarts.school.exceptions.FacultyNotFoundException;
import ru.hogwarts.school.exceptions.InvalidFacultySearchException;
import ru.hogwarts.school.mapper.FacultyMapper;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class FacultyService {

    private final FacultyMapper facultyMapper;
    private final FacultyRepository facultyRepository;

    public FacultyResponseDto addFaculty(final FacultyRequestDto dto) {
        log.info("Was invoked method for : {}", getMethodName());
        validateString(dto.name());
        log.debug("Faculty name verification passed");
        validateString(dto.color());
        log.debug("Faculty color verification passed");

        if (facultyRepository.existsFacultyByNameAndColor(StringUtils.capitalize(dto.name()),
                StringUtils.capitalize(dto.color()))) {
            log.warn("Faculty with the name \"{}\" already exists", dto.name());
            throw new FacultyAlreadyExistsException("Такой факультет уже существует");
        }

        var faculty = facultyMapper.toEntity(dto);
        log.debug("Saving faculty : {}", faculty.getName());

        try {
            return facultyMapper.toDto(facultyRepository.save(faculty));
        } catch (Exception e) {
            log.error("Failed to save faculty", e);
            throw e;
        }
    }

    public FacultyResponseDto getFacultyById(final Long id) {
        log.info("Was invoked method for : {}", getMethodName());
        log.debug("Getting faculty by ID : {}", id);

        return facultyMapper.toDto(facultyRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Faculty not found with ID : {}", id);
                    return new FacultyNotFoundException("Отсутствует факультет по данному ИД");
                }));

    }

    public FacultyResponseDto changeFaculty(final Long id, final FacultyRequestDto dto) {
        log.info("Was invoked method for : {}", getMethodName());
        validateString(dto.name());
        log.debug("Faculty name verification passed");
        validateString(dto.color());
        log.debug("Faculty color verification passed");

        if (!facultyRepository.existsById(id)) {
            log.error("Faculty not found with ID : {}", id);
            throw new FacultyNotFoundException("Факультет не найден");
        }

        var faculty = facultyMapper.toEntity(dto);
        log.debug("Changing faculty with ID : {}", faculty.getId());

        try {
            return facultyMapper.toDto(facultyRepository.save(faculty));
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

    public List<FacultyResponseDto> getFacultiesByColor(final String color) {
        log.info("Was invoked method for : {}", getMethodName());
        validateString(color);
        log.debug("Faculty color verification passed");

        var faculties = facultyRepository.findAllByColor(color);
        if (faculties.isEmpty()) {
            log.error("Faculty not found with color : {}", color);
            throw new FacultyNotFoundException("Факультеты с данным цветом не найдены");
        }
        return facultyMapper.toDto(faculties);
    }

    public FacultyResponseDto getFacultyByColorOrName(final String string) {
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
        return facultyMapper.toDto(faculties.get(new Random().nextInt(faculties.size())));
    }

    public FacultyResponseDto getFacultyByStudentIdOrName(final Long id, final String name) {
        log.info("Was invoked method for : {}", getMethodName());

        if (id != null && name != null) {
            log.debug("Search for faculty by id or name");

            return facultyMapper.toDto(facultyRepository.findByStudentsId(id)
                    .orElse(facultyRepository.findByStudentsName(name)
                            .orElseThrow(() -> {
                                log.error("Faculty not found with student ID : {} or student name : {}", id, name);
                                return new FacultyNotFoundException("Отсутствует факультет по данному ИД студента или имени студента");
                            })));
        } else if (id != null) {
            log.debug("Search for faculty by id");

            return facultyMapper.toDto(facultyRepository.findByStudentsId(id)
                    .orElseThrow(() -> {
                        log.error("Faculty not found with student ID : {}", id);
                        return new FacultyNotFoundException("Отсутствует факультет по данному ИД студента");
                    }));
        } else if (name != null) {
            validateString(name);
            log.debug("Student name verification passed");
            log.debug("Search for faculty by name");

            return facultyMapper.toDto(facultyRepository.findByStudentsName(name)
                    .orElseThrow(() -> {
                        log.error("Faculty not found with student name : {}", name);
                        return new FacultyNotFoundException("Отсутствует факультет по данному имени студента");
                    }));
        }
        log.error("Invalid faculty search: Neither student ID nor student name is specified");
        throw new InvalidFacultySearchException("Не указан ни ИД студента, ни имя студента");
    }

    public String getLongestFacultyName() {
        log.info("Was invoked method for : {}", getMethodName());
        log.debug("Search for faculties");
        var faculties = facultyRepository.findAll();

        if (faculties.isEmpty()) {
            log.error("Faculty not found");
            throw new FacultyNotFoundException("Факультеты не найдены");
        }

        return faculties.stream()
                .map(Faculty::getName)
                .max(Comparator.comparingInt(String::length))
                .get();
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
