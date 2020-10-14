package dev.myclinic.vertx.master.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB {

    private static String getConnectionString() {
        String host = System.getenv("MYCLINIC_DB_HOST");
        String user = System.getenv("MYCLINIC_DB_USER");
        String pass = System.getenv("MYCLINIC_DB_PASS");
        return String.format("jdbc:mysql://%s:3306/myclinic?" +
                        "user=%s&password=%s&zeroDateTimeBehavior=convertToNull&" +
                        "noDatetimeStringSync=true&useUnicode=true&" +
                        "characterEncoding=utf8&verifyServerCertificate=false&useSSL=true",
                host, user, pass);
    }

    public static Connection openConnection() throws ClassNotFoundException, SQLException {
        return DriverManager.getConnection(getConnectionString());
    }



}
