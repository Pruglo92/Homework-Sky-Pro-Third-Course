package ru.hogwarts.school.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hogwarts.school.exceptions.EmptyStringException;
import ru.hogwarts.school.exceptions.StudentAlreadyExistsException;
import ru.hogwarts.school.exceptions.StudentNotFoundException;
import ru.hogwarts.school.exceptions.WrongStudentAgeException;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;

    public Student addStudent(final Student student) {
        log.info("Was invoked method for : {}", getMethodName());
        validateString(student.getName());
        log.debug("Student name verification passed");
        validateAge(student.getAge());
        log.debug("Student age verification passed");

        if (studentRepository.existsByNameAndAge(StringUtils.capitalize(student.getName()),
                student.getAge())) {
            log.warn("Student with the name \"{}\" already exists", student.getName());
            throw new StudentAlreadyExistsException("Такой студент уже существует");
        }

        student.setId(student.getId());
        student.setName(StringUtils.capitalize(student.getName()));
        student.setAge(student.getAge());
        log.debug("Saving student : {}", student.getName());

        try {
            return studentRepository.save(student);
        } catch (Exception e) {
            log.error("Failed to save student", e);
            throw e;
        }
    }

    public Student getStudentById(final Long id) {
        log.info("Was invoked method for : {}", getMethodName());
        log.debug("Fetching student with ID : {}", id);

        return studentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Student not found with ID : {}", id);
                    return new StudentNotFoundException("Отсутствует Студент по данному ID");
                });
    }

    public Student changeStudentById(final Student student) {
        log.info("Was invoked method for : {}", getMethodName());
        validateString(student.getName());
        log.debug("Student name verification passed");
        validateAge(student.getAge());
        log.debug("Student age verification passed");

        if (!studentRepository.existsById(student.getId())) {
            log.error("Faculty not found with ID : {}", student.getId());
            throw new StudentNotFoundException("Студент не найден");
        }

        student.setName(StringUtils.capitalize(student.getName()));
        student.setAge(student.getAge());
        log.debug("Changing student with ID : {}", student.getId());

        try {
            return studentRepository.save(student);
        } catch (Exception e) {
            log.error("Failed to changing student", e);
            throw e;
        }
    }

    public void removeStudentById(final Long id) {
        log.info("Was invoked method for : {}", getMethodName());
        if (!studentRepository.existsById(id)) {
            log.error("Faculty not found with ID : {}", id);
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

    public List<Student> getStudentsByAge(final int age) {
        log.info("Was invoked method for : {}", getMethodName());
        validateAge(age);
        log.debug("Student age verification passed");

        return studentRepository.findAllByAge(age)
                .orElseThrow(() -> {
                    log.error("Students not found with age : {}", age);
                    return new StudentNotFoundException("Студенты с данным возрастом не найдены");
                });
    }

    public List<Student> getStudentsByAgeBetween(final int minAge, final int maxAge) {
        log.info("Was invoked method for : {}", getMethodName());
        validateAge(minAge);
        log.debug("Student minAge verification passed");
        validateAge(maxAge);
        log.debug("Student maxAge verification passed");

        return studentRepository.findByAgeBetween(minAge, maxAge)
                .orElseThrow(() -> {
                    log.error("No students found in this age range");
                    return new StudentNotFoundException("Студенты с данным промежутком возраста не найдены");
                });
    }

    public List<Student> getStudentsByFacultyId(final Long id) {
        log.info("Was invoked method for : {}", getMethodName());
        log.debug("Getting students by faculty ID : {}", id);

        return studentRepository.findAllByFaculty_Id(id)
                .orElseThrow(() -> {
                    log.error("Students not found with faculty ID : {}", id);
                    return new StudentNotFoundException("Отсутствуют Студенты по данному ID факультета");
                });
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

    public List<Student> getLastFiveStudents() {
        log.info("Was invoked method for : {}", getMethodName());
        log.debug("Getting the last five students");

        return studentRepository.getLastFiveStudents();
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
