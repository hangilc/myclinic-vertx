@mysql --port 13306 -u root -p%MYCLINIC_DB_ROOT_PASS% <%MYCLINIC_MYSQL_DATA_DIR%\sql-mode.sql
@mysql --port 13306 -u root -p%MYCLINIC_DB_ROOT_PASS% myclinic <%MYCLINIC_MYSQL_DATA_DIR%\mysql-base-data.sql
@mysql --port 13306 -u root -p%MYCLINIC_DB_ROOT_PASS% myclinic <%MYCLINIC_MYSQL_DATA_DIR%\mock-data.sql
