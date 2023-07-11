-- у каждого человека есть машина.
-- Причем несколько человек могут пользоваться одной машиной.
-- У каждого человека есть имя, возраст и признак того, что у него есть права (или их нет).
-- У каждой машины есть марка, модель и стоимость.
-- Также не забудьте добавить таблицам первичные ключи и связать их.

CREATE TABLE cars
(
    id    BIGSERIAL PRIMARY KEY,
    brand VARCHAR(16)             NOT NULL,
    model VARCHAR(16)             NOT NULL,
    price INT CHECK ( price > 0 ) NOT NULL
);

CREATE TABLE owners
(
    id             BIGSERIAL PRIMARY KEY,
    name           VARCHAR(16)                 NOT NULL,
    age            INT CHECK ( age > 17 )      NOT NULL,
    driver_license BOOLEAN DEFAULT TRUE        NOT NULL,
    cars_id        BIGINT REFERENCES cars (id) NOT NULL
);

INSERT INTO cars(brand, model, price)
VALUES ('ololo', 'car', 100500);

INSERT INTO owners(name, age, cars_id)
VALUES ('owner1', 20, 1),
       ('owner2', 30, 1);