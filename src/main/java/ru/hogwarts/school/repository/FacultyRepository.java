package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.hogwarts.school.model.Faculty;

import java.util.List;
import java.util.Optional;

@Repository
public interface FacultyRepository extends JpaRepository<Faculty, Long> {

    boolean existsFacultyByNameAndColor(String name, String color);

    List<Faculty> findAllByColor(String color);

    @Query("SELECT f FROM Faculty f WHERE LOWER(f.color) = LOWER(:color) OR LOWER(f.name) = LOWER(:name)")
    List<Faculty> findWithParam(@Param("color") String color, @Param("name") String name);

    Optional<Faculty> findByStudentsId(Long id);

    Optional<Faculty> findByStudentsName(String name);
}
