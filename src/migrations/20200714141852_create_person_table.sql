create table persons (
	id serial not null,
	name varchar(255) not null,
	email varchar(100),
	address text,
	date_of_birth date not null,
	constraint persons_pkey primary key (id)
);