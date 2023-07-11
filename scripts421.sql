-- Возраст студента не может быть меньше 16 лет.
-- Имена студентов должны быть уникальными и не равны нулю.
-- Пара “значение названия” - “цвет факультета” должна быть уникальной.
-- При создании студента без возраста ему автоматически должно присваиваться 20 лет.

ALTER table student
    ADD CONSTRAINT age_more_than_15 CHECK ( age >= 16 );
ALTER table student
    ALTER COLUMN name SET NOT NULL;
ALTER table student
    ADD CONSTRAINT unique_name UNIQUE (name);
ALTER table student
    ALTER COLUMN age SET DEFAULT 20;

ALTER TABLE faculty
    ADD CONSTRAINT unique_name_or_color UNIQUE (name, color);