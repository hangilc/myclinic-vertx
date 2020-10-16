package dev.myclinic.vertx.prescfax;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.myclinic.vertx.client2.Client;
import dev.myclinic.vertx.dto.HokenDTO;
import dev.myclinic.vertx.dto.PatientDTO;
import dev.myclinic.vertx.dto.TextDTO;
import dev.myclinic.vertx.dto.VisitDTO;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Presc {

    public final VisitDTO visit;
    @JsonProperty("presc_content")
    public final String prescContent;
    public final String fax;
    public final HokenDTO hoken;
    public final PatientDTO patient;

    public Presc(VisitDTO visit, String prescContent, String fax, HokenDTO hoken, PatientDTO patient) {
        this.visit = visit;
        this.prescContent = prescContent;
        this.fax = fax;
        this.hoken = hoken;
        this.patient = patient;
    }

    public static List<Presc> listPresc(Client client, LocalDate from, LocalDate upto) throws IOException, InterruptedException {
        List<Presc> result = new ArrayList<>();
        List<Integer> visitIds = client.listVisitIdInDateInterval(from, upto);
        for(int visitId: visitIds){
            List<TextDTO> texts = client.listText(visitId);
            String presc = null;
            Data.PharmaFax pharmaFax = null;
            boolean handled = false;
            for(TextDTO text: texts) {
                if (Data.isPrescContent(text.content)) {
                    if (presc != null) {
                        throw new RuntimeException("Multiple prescriptions.");
                    }
                    presc = text.content;
                    continue;
                }
                if (presc != null) {
                    Data.PharmaFax pf = Data.isPharmaFax(text.content);
                    if (pf != null) {
                        if (pharmaFax != null) {
                            throw new RuntimeException("Multiple pharma faxes.");
                        }
                        pharmaFax = pf;
                    } else if (Data.isPrescNotFax(text.content)) {
                        handled = true;
                    }
                }
            }
            if( presc != null && !handled ){
                if( pharmaFax == null ){
                    throw new RuntimeException(String.format("Unknown presc handling: visitId=%d", visitId));
                }
                VisitDTO visit = client.getVisit(visitId);
                HokenDTO hoken = client.getHoken(visitId);
                PatientDTO patient = client.getPatient(visit.patientId);
                result.add(new Presc(visit, presc, pharmaFax.fax, hoken, patient));
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return "Presc{" +
                "visit=" + visit +
                ", prescContent='" + prescContent + '\'' +
                ", fax='" + fax + '\'' +
                ", hoken=" + hoken +
                ", patient=" + patient +
                '}';
    }
}
