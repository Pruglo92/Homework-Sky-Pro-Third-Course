package ru.hogwarts.school.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.*;

@Getter
@Setter
@ToString(exclude = "student")
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Avatar extends BaseEntity {

    @Column
    private String filePath;
    @Column
    private long fileSize;
    @Column
    private String mediaType;
    @Column
    private byte[] data;

    @OneToOne
    @JoinColumn(name = "student_id")
    private Student student;
}
