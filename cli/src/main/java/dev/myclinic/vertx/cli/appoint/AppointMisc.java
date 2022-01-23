package dev.myclinic.vertx.cli.appoint;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static LocalDate readAppointDate(String str){
        Pattern pat = Pattern.compile("(\\d+-)?(\\d+)-(\\d+)");
        Matcher m = pat.matcher(str);
        if( m.matches() ){
            int year;
            if( m.group(1) == null ){
                year = LocalDate.now().getYear();
            } else {
                year = Integer.parseInt(m.group(1));
            }
            int month, day;
            month = Integer.parseInt(m.group(2));
            day = Integer.parseInt(m.group(3));
            return LocalDate.of(year, month, day);
        } else {
            throw new RuntimeException("Invalid date: " + str);
        }
    }

    public static LocalTime readAppointTime(String str){
        Pattern pat = Pattern.compile("(\\d+):(\\d+)");
        Matcher m = pat.matcher(str);
        if( m.matches() ){
            return LocalTime.of(
                    Integer.parseInt(m.group(1)),
                    Integer.parseInt(m.group(2))
            );
        } else {
            throw new RuntimeException("Invalid time: " + str);
        }
    }

}
