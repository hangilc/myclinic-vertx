package dev.myclinic.vertx.appoint;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AppointPersist {

    public static void enterAppoint(Connection conn, AppointDTO appoint)
            throws SQLException, JsonProcessingException {
        String sql = "insert into appoint values(?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, appoint.date.toString());
            stmt.setString(2, Misc.toSqlTime(appoint.time));
            if (appoint.patientName == null) {
                stmt.setNull(3, java.sql.Types.NULL);
            } else {
                stmt.setString(3, appoint.patientName);
            }
            if (appoint.patientId == null) {
                stmt.setNull(4, java.sql.Types.NULL);
            } else {
                stmt.setInt(4, appoint.patientId);
            }
            if (appoint.appointedAt == null) {
                stmt.setNull(5, java.sql.Types.NULL);
            } else {
                stmt.setString(5, Misc.toSqlDatetime(appoint.appointedAt));
            }
            stmt.executeUpdate();
        }
    }

    public static AppointDTO getAppoint(Connection conn, LocalDate atDate, LocalTime atTime)
            throws SQLException {
        String sql = "select * from appoint where date = ? and time = ?";
        List<AppointDTO> result = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, atDate.toString());
            stmt.setString(2, Misc.toSqlTime(atTime));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(resultSetToAppointDTO(rs));
                }
                if (result.size() == 1) {
                    return result.get(0);
                } else if (result.size() == 0) {
                    return null;
                } else {
                    String msg = String.format("Multiple appoints at %s %s.", atDate, atTime);
                    throw new RuntimeException(msg);
                }
            }
        }
    }

    public static int updateAppoint(Connection conn, AppointDTO appoint) throws SQLException {
        String sql = "update appoint set patient_name = ?, patient_id = ?, appointed_at = ? " +
                " where date = ? and time = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (appoint.patientName == null) {
                stmt.setNull(1, java.sql.Types.NULL);
            } else {
                stmt.setString(1, appoint.patientName);
            }
            if (appoint.patientId == null) {
                stmt.setNull(2, java.sql.Types.NULL);
            } else {
                stmt.setInt(2, appoint.patientId);
            }
            if (appoint.appointedAt == null) {
                stmt.setNull(3, java.sql.Types.NULL);
            } else {
                stmt.setString(3, Misc.toSqlDatetime(appoint.appointedAt));
            }
            stmt.setString(4, appoint.date.toString());
            stmt.setString(5, Misc.toSqlTime(appoint.time));
            return stmt.executeUpdate();
        }
    }

    public static int deleteAppoint(Connection conn, LocalDate date, LocalTime time) throws SQLException {
        String sql = "delete from appoint where date = ? and time - ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, date.toString());
            stmt.setString(2, Misc.toSqlTime(time));
            return stmt.executeUpdate();
        }
    }

    public static AppointDTO resultSetToAppointDTO(ResultSet rs)
            throws SQLException {
        return resultSetToAppointDTO(rs, 1);
    }

    public static AppointDTO resultSetToAppointDTO(ResultSet rs, int startIndex)
            throws SQLException {
        LocalDate date = rs.getObject(startIndex, LocalDate.class);
        LocalTime time = Misc.fromSqlTime(rs.getString(startIndex + 1));
        AppointDTO dto = new AppointDTO(date, time);
        dto.patientName = rs.getString(startIndex + 2);
        dto.patientId = rs.getInt(startIndex + 3);
        if (rs.wasNull()) {
            dto.patientId = null;
        }
        dto.appointedAt = Misc.fromSqlDatetime(rs.getString(startIndex + 4));
        return dto;
    }

}
