package dev.myclinic.vertx.appoint;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppointAPI {

    private static final ObjectMapper mapper = new ObjectMapper();

    private AppointAPI() {

    }

    public static void createAppointTime(Connection conn, LocalDate date, LocalTime time)
            throws SQLException, JsonProcessingException {
        AppointPersist.enterAppoint(conn, date, time);
        Map<String, Object> e = new HashMap<>();
        e.put("kind", "created");
        e.put("date", Misc.toSqlDate(date));
        e.put("time", Misc.toSqlTime(time));
        String body = mapper.writeValueAsString(e);
        AppointPersist.enterAppointEvent(conn, body);
    }

    public static void putAppoint(Connection conn, AppointDTO appoint)
            throws SQLException, JsonProcessingException {
        AppointDTO curr = AppointPersist.getAppoint(conn, appoint.date, appoint.time);
        if (curr == null) {
            String msg = String.format("Cannot make appoint at: %s %s", appoint.date, appoint.time);
            throw new RuntimeException(msg);
        }
        if (curr.patientName != null) {
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
        AppointPersist.enterAppointEvent(conn, body);
    }

    public static void cancelAppoint(Connection conn, LocalDate date, LocalTime time)
            throws SQLException, JsonProcessingException {
        AppointDTO app = AppointPersist.getAppoint(conn, date, time);
        if (app == null || app.patientName == null) {
            throw new RuntimeException("No such appoint.");
        }
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
        AppointPersist.updateAppoint(conn, app);
    }

    public static List<AppointDTO> listAppointTime(Connection conn, LocalDate from, LocalDate upto)
            throws SQLException {
        String sql = "select * from appoint where date >= ? and date <= ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, from.toString());
            stmt.setString(2, upto.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                return AppointPersist.resultSetToAppointList(rs);
            }
        }
    }

}
