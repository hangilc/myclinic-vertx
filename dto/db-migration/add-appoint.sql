create table appoint (
    appoint_date date not null,
    appoint_time time not null,
    patient_name varchar(255) not null,
    attributes json default null,
    primary key (appoint_date, appoint_time)
);
