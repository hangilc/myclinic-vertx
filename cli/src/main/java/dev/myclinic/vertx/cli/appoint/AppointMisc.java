package dev.myclinic.vertx.cli.appoint;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.function.Consumer;

class AppointMisc {

    private AppointMisc() {

    }

    public static Connection openConnection(){
        String dbFile = System.getenv("MYCLINIC_APPOINT_DB");
        if( dbFile == null ){
            throw new RuntimeException("Cannot find env var: MYCLINIC_APPOINT_DB");
        }
        String url = String.format("jdbc:sqlite:%s", dbFile);
        try {
            return DriverManager.getConnection(url);
        } catch (SQLException throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public interface SqlProc {
        void execute(Connection conn) throws Exception;
    }

    public static void withConnection(SqlProc consumer) {
        Connection conn = openConnection();
        try {
            conn.setAutoCommit(false);
            consumer.execute(conn);
            conn.commit();
        } catch(Exception ex){
            try {
                if( !conn.getAutoCommit() ) {
                    conn.rollback();
                }
                throw new RuntimeException(ex);
            } catch (SQLException th) {
                throw new RuntimeException(th);
            }
        } finally {
            try {
                conn.close();
            } catch(Throwable th){
                th.printStackTrace();
            }
        }
    }

}
