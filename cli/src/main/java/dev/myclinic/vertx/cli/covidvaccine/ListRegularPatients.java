package dev.myclinic.vertx.cli.covidvaccine;

import dev.myclinic.vertx.cli.Misc;
import dev.myclinic.vertx.db.MysqlDataSourceFactory;
import dev.myclinic.vertx.dto.PatientDTO;
import dev.myclinic.vertx.dto.VisitDTO;
import dev.myclinic.vertx.persist.PatientPersist;
import dev.myclinic.vertx.persist.VisitPersist;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.*;

import static java.util.stream.Collectors.toList;

public class ListRegularPatients {

    public static void main(String[] args) throws Exception {
        DataSource ds = Misc.getDataSource();
        Connection conn = ds.getConnection();
        try {
            String sql = "select p.* from visit as v inner join patient as p on v.patient_id = p.patient_id " +
                    "where date(v_datetime) >= '2020-11-25'";
            try(PreparedStatement stmt = conn.prepareStatement(sql)){
                ResultSet rs = stmt.executeQuery();
                List<PatientDTO> patients = new ArrayList<>();
                while(rs.next()){
                    PatientDTO patient = PatientPersist.resultSetToPatientDTO(rs);
                    patients.add(patient);
                }
                patients = uniquePatients(patients);
                patients.sort(Comparator.comparing((PatientDTO p) -> p.lastNameYomi)
                    .thenComparing((PatientDTO p) -> p.firstNameYomi));
                patients.forEach(p -> {
                    int age = Misc.ageAt(LocalDate.parse(p.birthday), LocalDate.of(2022, 3, 31));
                    String line = String.format("%04d %s%s %d %s", p.patientId, p.lastName, p.firstName,
                            age, p.phone);
                    System.out.println(line);
                });
            }
        } finally {
            conn.close();
        }
    }

    static List<PatientDTO> uniquePatients(List<PatientDTO> patients){
        Set<Integer> patientIds = new HashSet<>();
        return patients.stream().filter(p -> {
            int patientId = p.patientId;
            if( !patientIds.contains(patientId) ){
                patientIds.add(patientId);
                return true;
            } else {
                return false;
            }
        }).collect(toList());
    }

}
