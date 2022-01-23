create table appoint (
    date text not null,
    time text not null,
    patient_name text,
    patient_id integer not null,
    memo text not null,
    primary key(date, time)
);

create table appoint_event (
    id integer primary key,
    body text not null
);



