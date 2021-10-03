create table edaban (
    shahokokuho_id int(10) unsigned primary key,
    edaban char(2) not null,
    constraint foreign key (shahokokuho_id)
        references hoken_shahokokuho(shahokokuho_id)
        on delete cascade
);
