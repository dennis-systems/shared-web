create table db_injection
(
    id            int,
    script        varchar(255),
    sql           text,
    always_to_run bool default false,
    if_sql        TEXT,
    profile       varchar(50)
);

create unique index db_injection_id_uindex
    on db_injection (id);

alter table db_injection
    add constraint db_injection_pk
        primary key (id);

alter table db_injection
    add result          bool,
    add name            varchar(200),
    add fail_on_execute bool,
    add message         text;

create sequence db_injection_seq;

alter table db_injection
    add restart_on_fail bool;

