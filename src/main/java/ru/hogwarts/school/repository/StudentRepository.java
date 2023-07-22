package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.hogwarts.school.model.Student;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    boolean existsByNameAndAge(String name, int age);

    List<Student> findAllByAge(int age);

    List<Student> findByAgeBetween(int minAge, int maxAge);

    List<Student> findAllByFaculty_Id(Long id);

    @Query(value = "select count(*) from students", nativeQuery = true)
    Integer getStudentCount();

    @Query(value = "select avg(age) from students", nativeQuery = true)
    Integer getAverageAge();

    @Query(value = "select * from students order by id desc limit 5", nativeQuery = true)
    List<Student> getLastFiveStudents();
}
