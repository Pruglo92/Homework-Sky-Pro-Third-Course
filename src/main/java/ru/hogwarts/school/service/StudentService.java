package ru.hogwarts.school.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hogwarts.school.dto.StudentRequestDto;
import ru.hogwarts.school.dto.StudentResponseDto;
import ru.hogwarts.school.exceptions.EmptyStringException;
import ru.hogwarts.school.exceptions.StudentAlreadyExistsException;
import ru.hogwarts.school.exceptions.StudentNotFoundException;
import ru.hogwarts.school.exceptions.WrongStudentAgeException;
import ru.hogwarts.school.mapper.StudentMapper;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class StudentService {

    private final StudentMapper studentMapper;
    private final StudentRepository studentRepository;

    public StudentResponseDto addStudent(final StudentRequestDto dto) {
        log.info("Was invoked method for : {}", getMethodName());
        validateString(dto.name());
        log.debug("Student name verification passed");
        validateAge(dto.age());
        log.debug("Student age verification passed");

        if (studentRepository.existsByNameAndAge(StringUtils.capitalize(dto.name()),
                dto.age())) {
            log.warn("Student with the name \"{}\" already exists", dto.name());
            throw new StudentAlreadyExistsException("Такой студент уже существует");
        }

        var student = studentMapper.toEntity(dto);
        log.debug("Saving student : {}", student.getName());

        try {
            return studentMapper.toDto(studentRepository.save(student));
        } catch (Exception e) {
            log.error("Failed to save student", e);
            throw e;
        }
    }

    public StudentResponseDto getStudentById(final Long id) {
        log.info("Was invoked method for : {}", getMethodName());
        log.debug("Fetching student with ID : {}", id);

        return studentMapper.toDto(studentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Student not found with ID : {}", id);
                    return new StudentNotFoundException("Отсутствует Студент по данному ID");
                }));
    }

    public StudentResponseDto changeStudentById(final Long id, final StudentRequestDto dto) {
        log.info("Was invoked method for : {}", getMethodName());
        validateString(dto.name());
        log.debug("Student name verification passed");
        validateAge(dto.age());
        log.debug("Student age verification passed");

        if (!studentRepository.existsById(id)) {
            log.error("Student not found with ID : {}", id);
            throw new StudentNotFoundException("Студент не найден");
        }

        var student = studentMapper.toEntity(dto);
        student.setId(id);
        log.debug("Changing student with ID : {}", id);

        try {
            return studentMapper.toDto(studentRepository.save(student));
        } catch (Exception e) {
            log.error("Failed to changing student", e);
            throw e;
        }
    }

    public void removeStudentById(final Long id) {
        log.info("Was invoked method for : {}", getMethodName());
        if (!studentRepository.existsById(id)) {
            log.error("Student not found with ID : {}", id);
            throw new StudentNotFoundException("Отсутствует Студент по данному ID");
        }
        log.debug("Removing student with ID : {}", id);

        try {
            studentRepository.deleteById(id);
        } catch (Exception e) {
            log.error("Failed to removing student", e);
            throw e;
        }

    }

    public List<StudentResponseDto> getStudentsByAge(final int age) {
        log.info("Was invoked method for : {}", getMethodName());
        validateAge(age);
        log.debug("Student age verification passed");

        var students = studentRepository.findAllByAge(age);
        if (students.isEmpty()) {
            log.error("Students not found with age : {}", age);
            throw new StudentNotFoundException("Студенты с данным возрастом не найдены");
        }
        return studentMapper.toDto(students);
    }

    public List<StudentResponseDto> getStudentsByAgeBetween(final int minAge, final int maxAge) {
        log.info("Was invoked method for : {}", getMethodName());
        validateAge(minAge);
        log.debug("Student minAge verification passed");
        validateAge(maxAge);
        log.debug("Student maxAge verification passed");

        var students = studentRepository.findByAgeBetween(minAge, maxAge);
        if (students.isEmpty()) {
            log.error("No students found in this age range");
            throw new StudentNotFoundException("Студенты с данным промежутком возраста не найдены");
        }
        return studentMapper.toDto(students);
    }

    public List<StudentResponseDto> getStudentsByFacultyId(final Long id) {
        log.info("Was invoked method for : {}", getMethodName());
        log.debug("Getting students by faculty ID : {}", id);

        var students = studentRepository.findAllByFaculty_Id(id);
        if (students.isEmpty()) {
            log.error("Students not found with faculty ID : {}", id);
            throw new StudentNotFoundException("Отсутствуют Студенты по данному ID факультета");
        }
        return studentMapper.toDto(students);
    }

    public Integer getStudentCount() {
        log.info("Was invoked method for : {}", getMethodName());
        log.debug("Getting the number of all students");

        return studentRepository.getStudentCount();
    }

    public Integer getAverageAge() {
        log.info("Was invoked method for : {}", getMethodName());
        log.debug("Obtaining the average age of students");

        return studentRepository.getAverageAge();
    }

    public String getAverageAgeUsingStream() {
        log.info("Was invoked method for : {}", getMethodName());
        log.debug("Obtaining the average age of students");

        return String.valueOf((int) studentRepository.findAll().stream()
                .mapToInt(Student::getAge)
                .average()
                .orElseThrow(() -> {
                    log.error("Student not found");
                    return new StudentNotFoundException("Студенты отсутствуют");
                }));
    }

    public List<StudentResponseDto> getLastFiveStudents() {
        log.info("Was invoked method for : {}", getMethodName());
        log.debug("Getting the last five students");

        return studentMapper.toDto(studentRepository.getLastFiveStudents());
    }

    public List<String> getStudentNamesStartingWithA() {
        log.info("Was invoked method for : {}", getMethodName());

        return studentRepository.findAll().stream()
                .peek(item -> log.debug("Processing item in thread: {}", Thread.currentThread().getName()))
                .map(Student::getName)
                .filter(name -> StringUtils.startsWithIgnoreCase(name, "а") ||
                        StringUtils.startsWithIgnoreCase(name, "a"))
                .map(String::toUpperCase)
                .sorted()
                .toList();
    }

    private void validateString(String string) {
        log.info("Was invoked method for : {}", getMethodName());
        if (!StringUtils.isAlpha(string)) {
            throw new EmptyStringException("Запрос может содержать только буквы");
        }
    }

    private void validateAge(int age) {
        log.info("Was invoked method for : {}", getMethodName());
        if (age <= 0) {
            throw new WrongStudentAgeException("Возраст не может быть меньше 0");
        }
    }

    private static String getMethodName() {
        return convertCamelCaseToSpace(Thread.currentThread().getStackTrace()[2].getMethodName());
    }

    private static String convertCamelCaseToSpace(String camelCaseString) {
        return camelCaseString.replaceAll("(?<=.)([A-Z])", " $1").toLowerCase();
    }
}
