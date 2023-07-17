--liquibase formatted sql

--changeset pruglo-ve:20230716-1 failOnError:true
--comment: Create students_name_index.
CREATE INDEX IF NOT EXISTS students_name_index ON students (name);

--changeset pruglo-ve:20230716-2 failOnError:true
--comment: Create faculties_nc_index.
CREATE INDEX IF NOT EXISTS faculties_name_color_index ON faculties (name, color);