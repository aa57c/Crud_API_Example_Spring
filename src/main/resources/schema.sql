create table student
(
   id serial primary key not null,
   name varchar(255) not null,
   passport_number varchar(255) not null,
   primary key(id)
);