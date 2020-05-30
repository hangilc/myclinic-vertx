package dev.myclinic.vertx.db;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.myclinic.vertx.dto.PatientDTO;
import dev.myclinic.vertx.dto.PracticeLogDTO;
import dev.myclinic.vertx.practicelogevent.body.PatientCreated;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PatientTest extends TestBase {

//    private Connection conn;
//    private Backend backend;
//    private ObjectMapper mapper;
//
//    @BeforeEach
//    public void prepare() throws Exception {
//        this.conn = Config.getInstance().getConnection();
//        conn.setAutoCommit(false);
//        Query query = new Query(conn);
//        TableSet ts = Config.getInstance().getTableSet();
//        this.backend = new Backend(ts, query);
//        this.mapper = Config.getInstance().getMapper();
//    }
//
//    @AfterEach
//    public void cleanup() throws Exception {
//        if( this.conn != null ) {
//            this.conn.commit();
//            this.conn.close();
//        }
//    }

    @Test
    public void testEnter() throws Exception {
        PatientDTO patient = new PatientDTO();
        patient.lastName = "診療";
        patient.firstName = "太郎";
        patient.lastNameYomi = "しんりょう";
        patient.firstNameYomi = "たろう";
        patient.birthday = "1957-03-12";
        patient.sex = "M";
        patient.address = "東京";
        patient.phone = "03";
        int patientId = backend.enterPatient(patient);
        assertTrue(patientId > 0);
        patient.patientId = patientId;
        List<PracticeLogDTO> plogs = backend.getPracticeLogs();
        assertEquals(1, plogs.size());
        PracticeLogDTO plog = plogs.get(0);
        assertEquals("patient-created", plog.kind);
        assertEquals(patient, mapper.readValue(plog.body, PatientCreated.class).created);
        assertEquals(0, backend.getHotlineLogs().size());
        PatientDTO entered = backend.getPatient(patientId);
        assertEquals(entered, patient);
    }

}
