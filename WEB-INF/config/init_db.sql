create table resumes.public.resume
(
    uuid      char(36) not null
        constraint resume_pk
            primary key,
    full_name text
);

create table resumes.public.contact
(
    id          serial
        constraint contact_pk
            primary key,
    resume_uuid char(36) not null
        constraint contact_resume_uuid___fk
            references resumes.public.resume
            on update restrict on delete cascade,
    type        text     not null,
    value       text     not null
);
alter table public.contact
    owner to postgres;
create unique index contact_resume_uuid_type_index
    on public.contact (resume_uuid, type);



create table resumes.public.section
(
    id          serial
        constraint section_pk
            primary key,
    resume_uuid char(36) not null
        constraint section_resume_uuid_fk
            references resumes.public.resume
            on update restrict on delete cascade,
    type        text     not null,
    content     text     not null
);
alter table resumes.public.section
    owner to postgres;
create unique index section_resume_uuid_type_index
    on resumes.public.section (resume_uuid, type);



