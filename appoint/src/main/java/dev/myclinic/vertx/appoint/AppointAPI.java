package dev.myclinic.vertx.appoint;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppointAPI {

    private static final ObjectMapper mapper = new ObjectMapper();

    private AppointAPI() {

    }

    public static int createAppointTime(Connection conn, LocalDate date, LocalTime time)
            throws SQLException, JsonProcessingException {
        AppointPersist.enterAppoint(conn, date, time);
        Map<String, Object> e = new HashMap<>();
        e.put("kind", "created");
        e.put("date", Misc.toSqlDate(date));
        e.put("time", Misc.toSqlTime(time));
        String body = mapper.writeValueAsString(e);
        return AppointPersist.enterAppointEvent(conn, body);
    }

    public static int putAppoint(Connection conn, AppointDTO appoint)
            throws SQLException, JsonProcessingException {
        WithEventId<AppointDTO> curr = getAppoint(conn, appoint.date, appoint.time);
        if (curr == null) {
            String msg = String.format("Cannot make appoint at: %s %s", appoint.date, appoint.time);
            throw new RuntimeException(msg);
        }
        if (curr.value.patientName != null) {
            throw new RuntimeException("Appoint already exists.");
        }
        AppointPersist.updateAppoint(conn, appoint);
        Map<String, Object> e = new HashMap<>();
        e.put("kind", "updated");
        e.put("date", Misc.toSqlDate(appoint.date));
        e.put("time", Misc.toSqlTime(appoint.time));
        e.put("patient_name", appoint.patientName);
        e.put("patient_id", appoint.patientId);
        e.put("memo", appoint.memo);
        String body = mapper.writeValueAsString(e);
        return AppointPersist.enterAppointEvent(conn, body);
    }

    public static int cancelAppoint(Connection conn, LocalDate date, LocalTime time)
            throws SQLException, JsonProcessingException {
        WithEventId<AppointDTO> curr = getAppoint(conn, date, time);
        if (curr == null || curr.value.patientName == null) {
            throw new RuntimeException("No such appoint.");
        }
        AppointDTO app = curr.value;
        Map<String, Object> e = new HashMap<>();
        e.put("kind", "canceled");
        e.put("date", Misc.toSqlDate(app.date));
        e.put("time", Misc.toSqlTime(app.time));
        e.put("patient_name", app.patientName);
        e.put("patient_id", app.patientId);
        e.put("memo", app.memo);
        String body = mapper.writeValueAsString(e);
        AppointPersist.enterAppointEvent(conn, body);
        app.patientName = null;
        app.patientId = 0;
        app.memo = "";
        return AppointPersist.updateAppoint(conn, app);
    }

    public static class WithEventId<T> {
        public T value;
        public int eventId;

        public WithEventId(T value, int eventId) {
            this.value = value;
            this.eventId = eventId;
        }
    }

    public static WithEventId<AppointDTO> getAppoint(Connection conn, LocalDate atDate, LocalTime atTime)
            throws SQLException {
        String sql = "select * from appoint where date = ? and time = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, atDate.toString());
            stmt.setString(2, Misc.toSqlTime(atTime));
            try (ResultSet rs = stmt.executeQuery()) {
                if( rs.next() ){
                    AppointDTO app = AppointPersist.resultSetToAppointDTO(rs);
                    if( rs.next() ){
                        throw new RuntimeException("Cannot happen (mutltiple appoint).");
                    } else {
                        int eventId = getLastAppointEventId(conn);
                        return new WithEventId<>(app, eventId);
                    }
                } else {
                    return null;
                }
            }
        }
    }

    public static WithEventId<List<AppointDTO>> listAppointTime(Connection conn, LocalDate from, LocalDate upto)
            throws SQLException {
        String sql = "select * from appoint where date >= ? and date <= ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, from.toString());
            stmt.setString(2, upto.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                return new WithEventId<>(
                        AppointPersist.resultSetToAppointList(rs),
                        getLastAppointEventId(conn));
            }
        }
    }

    public static List<AppointEventDTO> listAppointEvent(Connection conn, int after, int before) throws SQLException {
        String sql = "select * from appoint_event where id > ? and id < ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, after);
            stmt.setInt(2, before);
            try(ResultSet rs = stmt.executeQuery()){
                List<AppointEventDTO> result = new ArrayList<>();
                while(rs.next()){
                    result.add(AppointPersist.resultSetToAppointEventDTO(rs));
                }
                return result;
            }
        }
    }

    private static int getLastAppointEventId(Connection conn) throws SQLException {
        String sql = "select id from appoint_event order by id desc limit 1";
        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            ResultSet rs = stmt.executeQuery();
            if( rs.next() ){
                return rs.getInt(1);
            } else {
                return 0;
            }
        }
    }

}
