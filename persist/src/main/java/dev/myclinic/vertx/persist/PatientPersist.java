package dev.myclinic.vertx.persist;

import dev.myclinic.vertx.dto.PatientDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PatientPersist {

    public static PatientDTO resultSetToPatientDTO(ResultSet rs, int startIndex)
            throws SQLException {
        PatientDTO dto = new PatientDTO();
        dto.patientId = rs.getInt(startIndex);
        dto.lastName = rs.getString(startIndex + 1);
        dto.firstName = rs.getString(startIndex + 2);
        dto.lastNameYomi = rs.getString(startIndex + 3);
        dto.firstNameYomi = rs.getString(startIndex + 4);
        dto.sex = rs.getString(startIndex + 5);
        dto.birthday = rs.getString(startIndex + 6);
        dto.address = rs.getString(startIndex + 7);
        dto.phone = rs.getString(startIndex + 8);
        return dto;
    }

    public static PatientDTO resultSetToPatientDTO(ResultSet rs)
            throws SQLException {
        return resultSetToPatientDTO(rs, 1);
    }

    public static PatientDTO getPatient(Connection conn, int patientId) throws SQLException {
        String sql = "select * from patient where patient_id = ?";
        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, patientId);
            try(ResultSet rs = stmt.executeQuery()){
                if( rs.next() ){
                    return resultSetToPatientDTO(rs);
                } else {
                    return null;
                }
            }
        }
    }

}
