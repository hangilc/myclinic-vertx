package dev.myclinic.vertx.persist;

import dev.myclinic.vertx.dto.VisitDTO;

import java.sql.ResultSet;
import java.sql.SQLException;

public class VisitPersist {

    public static VisitDTO resultSetToVisitDTO(ResultSet rs, int startIndex) throws SQLException {
        VisitDTO dto = new VisitDTO();
        dto.visitId = rs.getInt(startIndex);
        dto.patientId = rs.getInt(startIndex + 1);
        dto.visitedAt = rs.getString(startIndex + 2);
        dto.shahokokuhoId = rs.getInt(startIndex + 3);
        dto.koukikoureiId = rs.getInt(startIndex + 4);
        dto.roujinId = rs.getInt(startIndex + 5);
        dto.kouhi1Id = rs.getInt(startIndex + 6);
        dto.kouhi2Id = rs.getInt(startIndex + 7);
        dto.kouhi3Id = rs.getInt(startIndex + 8);
        dto.attributes = rs.getString(startIndex + 9);
        return dto;
    }

    public static VisitDTO resultSetToVisitDTO(ResultSet rs) throws SQLException {
        return resultSetToVisitDTO(rs, 1);
    }

}
