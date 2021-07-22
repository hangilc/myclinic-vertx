package dev.myclinic.vertx.appoint;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

class AppointPersist {

    public static void enterAppoint(Connection conn, LocalDate date, LocalTime time)
            throws SQLException {
        String sql = "insert into appoint values(?, ?, null, 0, '')";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, date.toString());
            stmt.setString(2, Misc.toSqlTime(time));
            stmt.executeUpdate();
        }
    }

    public static int updateAppoint(Connection conn, AppointDTO appoint) throws SQLException {
        String sql = "update appoint set patient_name = ?, patient_id = ?, memo = ? " +
                " where date = ? and time = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (appoint.patientName == null) {
                stmt.setNull(1, java.sql.Types.NULL);
            } else {
                stmt.setString(1, appoint.patientName);
            }
            stmt.setInt(2, appoint.patientId);
            stmt.setString(3, appoint.memo);
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

    public static void enterAppointEvent(Connection conn, String body) throws SQLException {
        String sql = "insert into appoint_event(body) values(?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, body);
            stmt.executeUpdate();
        }
    }

    public static AppointDTO resultSetToAppointDTO(ResultSet rs)
            throws SQLException {
        AppointDTO dto = new AppointDTO();
        dto.date = LocalDate.parse(rs.getString(1));
        dto.time = Misc.fromSqlTime(rs.getString(2));
        dto.patientName = rs.getString(3);
        dto.patientId = rs.getInt(4);
        dto.memo = rs.getString(5);
        return dto;
    }

    public static List<AppointDTO> resultSetToAppointList(ResultSet rs) throws SQLException {
        List<AppointDTO> list = new ArrayList<>();
        while(rs.next()){
            AppointDTO app = resultSetToAppointDTO(rs);
            list.add(app);
        }
        return list;
    }

}
