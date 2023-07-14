package ru.hogwarts.school.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@Entity
@ToString(exclude = "faculty")
@Table(name = "students")
@NoArgsConstructor
@AllArgsConstructor
public class Student extends BaseEntity {

    @Column
    @JsonProperty("name")
    private String name;
    @Column
    @JsonProperty("age")
    private int age;
    @ManyToOne
    @JoinColumn(name = "faculty_id")
    private Faculty faculty;
    @OneToOne(mappedBy = "student")
    private Avatar avatar;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return age == student.age && name.equals(student.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, age);
    }
}
