create table appoint_log (
    appoint_log_id int not null primary key auto_increment,
    created_at datetime not null,
    log_data json not null
);