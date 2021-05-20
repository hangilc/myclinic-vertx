package dev.myclinic.vertx.appoint;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class AppointPersist {

    public static void enterAppoint(Connection conn, ObjectMapper mapper, AppointDTO appoint)
            throws SQLException, JsonProcessingException {
        String sql = "insert into appoint values(?, ?, ?, ?)";
        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, appoint.appointDate.toString());
            stmt.setString(2, Misc.toSqlTime(appoint.appointTime));
            stmt.setString(3, appoint.patientName);
            stmt.setString(4, appoint.getAttrsAsJson(mapper));
            stmt.executeUpdate();
        }
    }

    public static void cancelAppoint(Connection conn, AppointDTO appoint) throws SQLException {
        String sql = "delete from appoint where appoint_date = ? and appoint_time = ?";
        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, appoint.appointDate.toString());
            stmt.setString(2, Misc.toSqlTime(appoint.appointTime));
            stmt.executeUpdate();
        }
    }

    public static void logAppointCreated(Connection conn, ObjectMapper mapper, AppointDTO created)
            throws SQLException, JsonProcessingException {
        AppointLogDTO log = AppointLogDTO.created(mapper, created);
        String sql = "insert into appoint_log values(?, ?, ?)";
        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, 0);
            stmt.setObject(2, Misc.toSqlDatetime(log.createdAt));
            stmt.setString(3, mapper.writeValueAsString(log.logData));
            stmt.executeUpdate();
        }
    }

    public static void logAppointCanceled(Connection conn, ObjectMapper mapper, AppointDTO canceled)
            throws SQLException, JsonProcessingException {
        AppointLogDTO log = AppointLogDTO.canceled(mapper, canceled);
        String sql = "insert into appoint_log values(?, ?, ?)";
        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, 0);
            stmt.setObject(2, Misc.toSqlDatetime(log.createdAt));
            stmt.setString(3, mapper.writeValueAsString(log.logData));
            stmt.executeUpdate();
        }
    }

    public static AppointDTO resultSetToAppointDTO(ResultSet rs) throws SQLException {
        int startIndex = 1;
        AppointDTO dto = new AppointDTO();
        dto.appointDate = rs.getObject(startIndex + 0, LocalDate.class);
        dto.appointTime = rs.getObject(startIndex + 1, LocalTime.class);
        dto.patientName = rs.getString(startIndex + 2);
        dto.attributes = rs.getObject(startIndex + 3, );
    }

    public static AppointDTO getAppoint(Connection conn, ObjectMapper mapper, LocalDate atDate, LocalTime atTime)
            throws SQLException {
        String sql = "select * from appoint where appoint_date = ? and appoint_time = ?";
        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, atDate.toString());
            stmt.setString(2, Misc.toSqlTime(atTime));
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                AppointDTO dto = new AppointDTO();
                rs.getObject(1, LocalDate.class);

            }
        }

    }

    public static List<AppointDTO> listAppoint(Connection conn, ObjectMapper mapper, LocalDate from, LocalDate upto)
            throws SQLException, JsonProcessingException{

    }

}
