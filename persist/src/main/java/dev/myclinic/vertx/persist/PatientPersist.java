package dev.myclinic.vertx.persist;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.myclinic.vertx.appoint.AppointDTO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

public class PatientPersist {

    public static PatientDTO resultSetToPatientDTO(){
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


    }

}
