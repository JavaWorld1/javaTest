

INSERT INTO postgres.public.resume (uuid, full_name)
VALUES ('7e9d4f75-9b39-4203-9863-c7843d0157e3', 'Vasek'),
       ('53d5becb-38b4-4994-b8a7-9698a1eb1881', 'Zhora'),
       ('811d52c4-f002-4e1a-8fd1-01ee7e79331d', 'Tomas');


INSERT INTO postgres.public.contact (type, value, resume_uuid)
VALUES ('PHONE', '123456', '7e9d4f75-9b39-4203-9863-c7843d0157e3');
INSERT INTO postgres.public.contact (type, value, resume_uuid)
VALUES ('SKYPE', 'skype_er', '7e9d4f75-9b39-4203-9863-c7843d0157e3');


create table postgres.public.resume
(
    uuid      char(36) not null
        constraint resume_pk
            primary key,
    full_name text
);



create table postgres.public.contact
(
    id          serial
        constraint contact_pk
            primary key,
    resume_uuid char(36) not null
        constraint contact_resume_uuid___fk
            references postgres.public.resume
            on update restrict on delete cascade,
    type        text     not null,
    value       text     not null
);
alter table public.contact
    owner to postgres;
create unique index contact_resume_uuid_type_index
    on public.contact (resume_uuid, type);



create table postgres.public.section
(
    id          serial
        constraint section_pk
            primary key,
    resume_uuid char(36) not null
        constraint section_resume_uuid_fk
            references postgres.public.resume
            on update restrict on delete cascade,
    type        text     not null,
    content     text     not null
);
alter table postgres.public.section
    owner to postgres;
create unique index section_resume_uuid_type_index
    on postgres.public.section (resume_uuid, type);



