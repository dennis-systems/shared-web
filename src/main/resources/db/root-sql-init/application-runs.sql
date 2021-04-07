create table applications_runs
(
	id serial
		constraint applications_runs_pk
			primary key,
	date timestamp default current_timestamp,
	application varchar(200)
);

