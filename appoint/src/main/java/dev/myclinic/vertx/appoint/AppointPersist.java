package dev.myclinic.vertx.appoint;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
            stmt.setString(3, log.logData);
            stmt.executeUpdate();
        }
    }

}
