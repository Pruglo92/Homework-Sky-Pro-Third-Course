package ru.hogwarts.school.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.exceptions.EmptyStringException;
import ru.hogwarts.school.exceptions.FacultyAlreadyExistsException;
import ru.hogwarts.school.exceptions.FacultyNotFoundException;
import ru.hogwarts.school.model.Faculty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class FacultyService {
    private static final AtomicLong count = new AtomicLong(0);
    Map<Long, Faculty> facultyMap = new HashMap<>();

    public Faculty addFaculty(final String name, final String color) {
        validateString(name);
        validateString(color);

        Faculty faculty = new Faculty(name, color);
        if (facultyMap.containsValue(faculty)) {
            // Если объект уже содержится в карте, не добавляем его повторно
            throw new FacultyAlreadyExistsException("Такой факультет уже существует");
        }
        var key = generateUniqueKey();
        facultyMap.put(key, faculty);
        faculty.setId(key); // Установить id факультета
        return facultyMap.get(key);
    }

    private synchronized Long generateUniqueKey() {
        // Генерировать уникальный ключ, который не присутствует в карте facultyMap
        var key = count.incrementAndGet();
        while (facultyMap.containsKey(key)) {
            key = count.incrementAndGet();
        }
        return key;
    }

    public Faculty getFacultyById(final Long id) {
        if (id > facultyMap.size()) {
            throw new FacultyNotFoundException("Отсутствует Факультет по данному ID");
        }
        return facultyMap.get(id);
    }

    public Faculty changeFacultyById(final Long id, final String name, final String color) {
        if (id > facultyMap.size()) {
            throw new FacultyNotFoundException("Отсутствует Факультет по данному ID");
        }
        validateString(name);
        validateString(color);

        Faculty faculty = new Faculty(name, color);
        faculty.setId(id);

        return facultyMap.put(id, faculty);
    }

    public Faculty removeFacultyById(final Long id) {
        if (id > facultyMap.size()) {
            throw new FacultyNotFoundException("Отсутствует Факультет по данному ID");
        }
        return facultyMap.remove(id);
    }

    private void validateString(String string) {
        if (!StringUtils.isAlpha(string)) {
            throw new EmptyStringException("Запрос может содержать только буквы");
        }
    }

    public List<Faculty> getFacultiesByAge(String color) {
        validateString(color);
        var faculties = facultyMap.values().stream()
                .filter(faculty -> Objects.equals(faculty.getColor(), color))
                .toList();
        if (faculties.isEmpty()) {
            throw new FacultyNotFoundException("Факультеты с данным цветом не найдены");
        }
        return faculties;
    }
}
