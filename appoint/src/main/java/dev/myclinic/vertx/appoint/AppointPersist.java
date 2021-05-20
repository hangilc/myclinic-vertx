package dev.myclinic.vertx.appoint;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
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

    public static AppointDTO resultSetToAppointDTO(ResultSet rs, ObjectMapper mapper)
            throws SQLException, JsonProcessingException {
        int startIndex = 1;
        AppointDTO dto = new AppointDTO();
        dto.appointDate = rs.getObject(startIndex, LocalDate.class);
        dto.appointTime = rs.getObject(startIndex + 1, LocalTime.class);
        dto.patientName = rs.getString(startIndex + 2);
        String attr = rs.getString(startIndex + 3);
        dto.attributes = attr == null ? null : mapper.readValue(attr, new TypeReference<>(){});
        return dto;
    }

    public static AppointDTO getAppoint(Connection conn, ObjectMapper mapper, LocalDate atDate, LocalTime atTime)
            throws SQLException, JsonProcessingException {
        String sql = "select * from appoint where appoint_date = ? and appoint_time = ?";
        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            AppointDTO appoint = null;
            stmt.setString(1, atDate.toString());
            stmt.setString(2, Misc.toSqlTime(atTime));
            try(ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    appoint = resultSetToAppointDTO(rs, mapper);
                    if (rs.next()) {
                        String msg = String.format("Multiple result. getAppoint. %s %s",
                                atDate.toString(), Misc.toSqlTime(atTime));
                        throw new RuntimeException(msg);
                    }
                }
                return appoint;
            }
        }
    }

    public static List<AppointDTO> listAppoint(Connection conn, ObjectMapper mapper, LocalDate from, LocalDate upto)
            throws SQLException, JsonProcessingException{
        String sql = "select * from appoint where appoint_date >= ? and appoint_date <= ?";
        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, from.toString());
            stmt.setString(2, upto.toString());
            List<AppointDTO> result = new ArrayList<>();
            try(ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    AppointDTO appoint = resultSetToAppointDTO(rs, mapper);
                    result.add(appoint);
                }
                return result;
            }
        }
    }

}
