create table if not exists appoint (
    date text not null,
    time text not null,
    patient_name text,
    patient_id integer,
    appointed_at text,
    primary key(date, time)
);

