CREATE schema `users_service_db`;
-- CREATE table `users_service_db`.`users` (
-- id BIGINT primary key not null,
-- username varchar(50) unique not null,
-- email varchar(100) unique not null,
-- password_hash varchar(255) not null,
-- first_name varchar(50) not null,
-- last_name varchar(50) not null,
-- created_at timestamp default current_timestamp
-- );
CREATE schema `transaction_service_db`;
-- create table `transaction_service_db`.`transaction`(
-- id BIGINT not null primary key,
-- from_account_id BIGINT not null,
-- to_account_id BIGINT not null,
-- amount decimal(15,2) not null,
-- description varchar(255),
-- status enum('initiated','success','failed') default 'initiated',
-- timestamp timestamp default current_timestamp
-- );
CREATE schema `account_service_db`;
-- create table `account_service_db`.`accounts` (
-- id BIGINT not null primary key,
-- user_id BIGINT not null,
-- account_number varchar(20) unique not null,
-- account_type enum('savings','checking') not null,
-- balance decimal(15,2) default 0.00,
-- status enum('active','inactive') default 'active',
-- created_at timestamp default current_timestamp,
-- updated_at timestamp default current_timestamp on update current_timestamp
-- );
CREATE schema `logging_service_db`;
-- create table `logging_service_db`.`logs` (
-- id BIGINT primary key auto_increment,
-- message text not null,
-- message_type enum('request','response') not null,
-- date_time timestamp not null
-- );
