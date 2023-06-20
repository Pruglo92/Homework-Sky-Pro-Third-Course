package ru.hogwarts.school.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.exceptions.EmptyStringException;
import ru.hogwarts.school.exceptions.StudentAlreadyExistsException;
import ru.hogwarts.school.exceptions.StudentNotFoundException;
import ru.hogwarts.school.exceptions.WrongStudentAgeException;
import ru.hogwarts.school.model.Student;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class StudentService {
    private static final AtomicLong count = new AtomicLong(0);
    Map<Long, Student> studentsMap = new HashMap<>();

    public Student addStudent(final String name, final int age) {
        validateString(name);
        validateAge(age);

        Student student = new Student(name, age);
        if (studentsMap.containsValue(student)) {
            // Если объект уже содержится в карте, не добавляем его повторно
            throw new StudentAlreadyExistsException("Такой Студент уже существует");
        }
        var key = generateUniqueKey();
        studentsMap.put(key, student);
        student.setId(key); // Установить id факультета
        return studentsMap.get(key);
    }

    private synchronized Long generateUniqueKey() {
        // Генерировать уникальный ключ, который не присутствует в карте facultyMap
        var key = count.incrementAndGet();
        while (studentsMap.containsKey(key)) {
            key = count.incrementAndGet();
        }
        return key;
    }

    public Student getStudentById(final Long id) {
        if (id > studentsMap.size()) {
            throw new StudentNotFoundException("Отсутствует Студент по данному ID");
        }
        return studentsMap.get(id);
    }

    public Student changeStudentById(final Long id, final String name, final int age) {
        if (id > studentsMap.size()) {
            throw new StudentNotFoundException("Отсутствует Студент по данному ID");
        }
        validateString(name);
        validateAge(age);

        Student student = new Student(name, age);
        student.setId(id);

        return studentsMap.put(id, student);
    }

    public Student removeStudentById(final Long id) {
        if (id > studentsMap.size()) {
            throw new StudentNotFoundException("Отсутствует Студент по данному ID");
        }
        return studentsMap.remove(id);
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
        List<Student> students = studentsMap.values().stream()
                .filter(student -> student.getAge() == age)
                .toList();

        if (students.isEmpty()) {
            throw new StudentNotFoundException("Студенты с данным возрастом не найдены");
        }
        return students;
    }
}
