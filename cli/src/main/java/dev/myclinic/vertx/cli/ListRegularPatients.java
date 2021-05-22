package dev.myclinic.vertx.cli;

import dev.myclinic.vertx.db.MysqlDataSourceFactory;

import javax.sql.DataSource;
import java.sql.Connection;

public class ListRegularPatients {

    public static void main(String[] args) throws Exception {
        DataSource ds = MysqlDataSourceFactory.create();
        Connection conn = ds.getConnection();
        try {
            System.out.println(conn);
        } finally {
            conn.close();
        }
    }

}
