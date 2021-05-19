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
            stmt.setString(2, appoint.appointTime.toString());
            stmt.setString(3, appoint.patientName);
            stmt.setString(4, appoint.getAttrsAsJson(mapper));
            int res = stmt.executeUpdate();
            System.out.println(res);
        }
    }

}
