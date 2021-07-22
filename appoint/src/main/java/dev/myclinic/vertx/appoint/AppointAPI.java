package dev.myclinic.vertx.appoint;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class AppointAPI {

    private AppointAPI() {

    }

    public static void createAppointTime(Connection conn, LocalDate date, LocalTime time)
            throws SQLException {
        AppointDTO app = new AppointDTO(date, time);
        AppointPersist.enterAppoint(conn, app);
    }

    public static void putAppoint(Connection conn, LocalDate date, LocalTime time, String patientName)
            throws SQLException {
        AppointDTO app = AppointPersist.getAppoint(conn, date, time);
        if( app == null ){
            String msg = String.format("Cannot make appoint at: %s %s", date, time);
            throw new RuntimeException(msg);
        }
        if( app.patientName != null || app.patientId != null ){
            throw new RuntimeException("Appoint already exists.");
        }
        app.patientName = patientName;
        app.appointedAt = LocalDateTime.now();
        int mod = AppointPersist.updateAppoint(conn, app);
        if( mod != 1 ){
            throw new RuntimeException("Update appoint failed.");
        }
    }

    public static void cancelAppoint(Connection conn, LocalDate date, LocalTime time) throws SQLException {
        AppointDTO app = AppointPersist.getAppoint(conn, date, time);
        if( app == null || app.patientName == null ){
            throw new RuntimeException("No such appoint.");
        }
        AppointCancelDTO cancel = new AppointCancelDTO();
        cancel.date = app.date;
        cancel.time = app.time;
        cancel.patientName = app.patientName;
        cancel.patientId = app.patientId;
        cancel.canceledAt = LocalDateTime.now();
        AppointPersist.enterCancel(conn, cancel);
        app.patientName = null;
        app.patientId = null;
        app.appointedAt = null;
        AppointPersist.updateAppoint(conn, app);
    }

    public static List<AppointDTO> listAppointTime(Connection conn, LocalDate from, LocalDate upto)
            throws SQLException {
        String sql = "select * from appoint where date >= ? and date <= ?";
        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, from.toString());
            stmt.setString(2, upto.toString());
            try(ResultSet rs = stmt.executeQuery()){
                return AppointPersist.resultSetToAppointList(rs);
            }
        }
    }



}
