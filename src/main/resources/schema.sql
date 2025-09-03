create table student
(
   id serial primary key not null,
   name varchar(100) not null,
   passport_number varchar(10) not null unique,
   age integer not null check (age >= 16 and age <= 100),
   email varchar(255) unique,
   enrollment_date timestamp not null,
   graduation_year integer check (graduation_year >= 2020 and graduation_year <= 2030),
   status varchar(20) not null default 'ACTIVE',
   created_at timestamp not null,
   updated_at timestamp not null,
   version bigint not null default 0,
   primary key(id)
);