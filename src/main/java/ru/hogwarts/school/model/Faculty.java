package ru.hogwarts.school.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Entity
@ToString(exclude = "students")
@Table(name = "faculties")
@NoArgsConstructor
@AllArgsConstructor
public class Faculty extends BaseEntity {

    @Column
    @JsonProperty("name")
    private String name;
    @Column
    @JsonProperty("color")
    private String color;
    @OneToMany(mappedBy = "faculty")
    @JsonIgnore
    private List<Student> students;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Faculty faculty = (Faculty) o;
        return name.equals(faculty.name) && color.equals(faculty.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, color);
    }
}
