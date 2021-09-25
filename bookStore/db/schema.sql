CREATE DATABASE IF NOT EXISTS `bookstoresystem`;
USE `bookstoresystem`;

CREATE TABLE IF NOT EXISTS `book`
(
    id int NOT NULL AUTO_INCREMENT primary key,
    title varchar(30),
    author varchar(20),
    year int,
    price double,
    amount int
);

CREATE TABLE IF NOT EXISTS `invoice`
(
    id int NOT NULL AUTO_INCREMENT primary key,
    invoice_date date,
    invoice_date_time time,
    invoice_total double
);

CREATE TABLE IF NOT EXISTS `invoice_line_item`
(
    id int NOT NULL AUTO_INCREMENT primary key,
    book_title varchar(30),
    book_author varchar(20),
    book_price double,
    discount double,
    amount int,
    summed_costs double,
    invoice_id int,
    bought boolean,
    foreign key (invoice_id) references `invoice`(id)
);
