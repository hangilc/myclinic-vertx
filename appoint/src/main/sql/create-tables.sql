create table if not exists appoint (
    date text not null,
    time text not null,
    patient_name text,
    patient_id integer,
    appointed_at text,
    primary key(date, time)
);

create table if not exists appoint_cancel (
    date text not null,
    time text not null,
    patient_name text not null,
    patient_id integer,
    canceled_at text not null,
    primary key(date, time, patient_name, canceled_at)
);



