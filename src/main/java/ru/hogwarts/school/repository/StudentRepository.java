package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.hogwarts.school.model.Student;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    boolean existsByNameAndAge(String name, int age);

    Optional<List<Student>> findAllByAge(int age);

    Optional<List<Student>> findByAgeBetween(int minAge, int maxAge);

    Optional<List<Student>> findAllByFaculty_Id(Long id);

    @Query(value = "select count(*) from student", nativeQuery = true)
    Integer getStudentCount();

    @Query(value = "select avg(age) from student", nativeQuery = true)
    Integer getAverageAge();

    @Query(value = "select * from student order by id desc limit 5", nativeQuery = true)
    List<Student> getLastFiveStudents();
}
