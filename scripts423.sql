 -- Составить первый JOIN-запрос,
 -- чтобы получить информацию обо всех студентах
 -- (достаточно получить только имя и возраст студента) школы Хогвартс вместе с названиями факультетов.
 -- Составить второй JOIN-запрос, чтобы получить только тех студентов, у которых есть аватарки.

SELECT s.name, s.age , s.id, f.name
FROM student s
    LEFT JOIN faculty f ON s.faculty_id = f.id;

SELECT s.name, s.age, a.id
FROM student s
    INNER JOIN avatar a ON s.id = a.student_id;