package dev.myclinic.vertx.cli;

import dev.myclinic.vertx.db.MysqlDataSourceFactory;
import dev.myclinic.vertx.util.DateTimeUtil;

import javax.sql.DataSource;
import java.time.LocalDate;

class Misc {

    public static DataSource getDataSource(){
        return MysqlDataSourceFactory.create();
    }

    public static int ageAt(LocalDate birthday, LocalDate at){
        return DateTimeUtil.calcAge(birthday, at);
    }
}
