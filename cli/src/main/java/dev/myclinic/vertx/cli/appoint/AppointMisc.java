package dev.myclinic.vertx.cli.appoint;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

class AppointMisc {

    private AppointMisc() {

    }

    public static Connection openConnection() {
        String dbFile = System.getenv("MYCLINIC_APPOINT_DB");
        if( dbFile == null ){
            throw new RuntimeException("Cannot find env var: MYCLINIC_APPOINT_DB");
        }
        String url = String.format("jdbc:sqlite:%s", dbFile);
        try {
            return DriverManager.getConnection(url);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

}
