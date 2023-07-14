package ru.hogwarts.school.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@ToString(exclude = "student")
@Table(name = "avatars")
@NoArgsConstructor
@AllArgsConstructor
public class Avatar extends BaseEntity {

    @Column
    private String filePath;
    @Column
    private long fileSize;
    @Column
    private String mediaType;
    @Lob
    @Column
    private byte[] data;
    @OneToOne
    @JoinColumn(name = "student_id")
    private Student student;
}
