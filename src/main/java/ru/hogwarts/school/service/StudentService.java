package ru.hogwarts.school.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.exceptions.EmptyStringException;
import ru.hogwarts.school.exceptions.StudentAlreadyExistsException;
import ru.hogwarts.school.exceptions.StudentNotFoundException;
import ru.hogwarts.school.exceptions.WrongStudentAgeException;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;

    public Student addStudent(final Student student) {
        validateString(student.getName());
        validateAge(student.getAge());

        if (studentRepository.existsByNameAndAge(StringUtils.capitalize(student.getName()),
                student.getAge())) {
            throw new StudentAlreadyExistsException("Такой студент уже существует");
        }

        student.setId(student.getId());
        student.setName(StringUtils.capitalize(student.getName()));
        student.setAge(student.getAge());
        return studentRepository.save(student);
    }

    public Student getStudentById(final Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException("Отсутствует Студент по данному ID"));
    }

    public Student changeStudentById(final Student student) {
        validateString(student.getName());
        validateAge(student.getAge());

        if (!studentRepository.existsById(student.getId())) {
            throw new StudentNotFoundException("Студент не найден");
        }

        student.setName(StringUtils.capitalize(student.getName()));
        student.setAge(student.getAge());

        return studentRepository.save(student);
    }

    public void removeStudentById(final Long id) {
        if (!studentRepository.existsById(id)) {
            throw new StudentNotFoundException("Отсутствует Студент по данному ID");
        }
        studentRepository.deleteById(id);
    }

    private void validateString(String string) {
        if (!StringUtils.isAlpha(string)) {
            throw new EmptyStringException("Запрос может содержать только буквы");
        }
    }

    private void validateAge(int age) {
        if (age <= 0) {
            throw new WrongStudentAgeException("Возраст не может быть меньше 0");
        }
    }

    public List<Student> getStudentsByAge(final int age) {
        validateAge(age);

        return studentRepository.findAllByAge(age)
                .orElseThrow(() -> new StudentNotFoundException("Студенты с данным возрастом не найдены"));
    }
}
