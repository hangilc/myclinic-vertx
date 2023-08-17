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

    public Presc(@JsonProperty("visit") VisitDTO visit,
                 @JsonProperty("presc_content") String prescContent,
                 @JsonProperty("fax") String fax,
                 @JsonProperty("hoken") HokenDTO hoken,
                 @JsonProperty("patient") PatientDTO patient) {
        this.visit = visit;
        this.prescContent = prescContent;
        this.fax = fax;
        this.hoken = hoken;
        this.patient = patient;
    }

    public static class PrescError extends RuntimeException {
        public String kind;
        public String message;
        public int visitId;

        public PrescError(String kind, String message, int visitId) {
            this.kind = kind;
            this.message = message;
            this.visitId = visitId;
        }
    }

    private static Presc probeVisit(Client client, int visitId){
        List<TextDTO> texts = client.listText(visitId);
        String presc = null;
        boolean is0410 = false;
        boolean isOnline = false;
        Data.PharmaFax pharmaFax = null;
        boolean handled = false;
        for(TextDTO text: texts) {
            if (Data.isPrescContent(text.content)) {
                if (presc != null) {
                    throw new PrescError("multiple-prescriptions",
                            "複数の処方箋が発行されています。",
                            visitId);
                    //throw new RuntimeException("Multiple prescriptions.");
                }
                presc = text.content;
                if( Data.is0410Presc(text.content) ){
                    is0410 = true;
                }
                if( Data.isOnline(text.content) ){
                    isOnline = true;
                }
                continue;
            }
            if (presc != null) {
                Data.PharmaFax pf = Data.isPharmaFax(text.content);
                if (pf != null) {
                    if (pharmaFax != null) {
                        throw new PrescError("multiple-pharmacies",
                                "薬局が複数設定されています。",
                                visitId);
                        //throw new RuntimeException("Multiple pharma faxes.");
                    }
                    pharmaFax = pf;
                } else if (Data.isPrescNotFax(text.content)) {
                    handled = true;
                }
            }
        }
        if( presc != null && (is0410 || isOnline) && !handled ){
            if( pharmaFax == null ){
                throw new PrescError("missing-pharmacy", "ファックス先の薬局が指定されていません。", visitId);
                //throw new RuntimeException(String.format("Unknown presc handling: visitId=%d", visitId));
            }
            VisitDTO visit = client.getVisit(visitId);
            HokenDTO hoken = client.getHoken(visitId);
            PatientDTO patient = client.getPatient(visit.patientId);
            return new Presc(visit, presc, pharmaFax.fax, hoken, patient);
        } else {
            return null;
        }
    }

    public static List<Presc> listPresc(Client client, LocalDate from, LocalDate upto) throws IOException, InterruptedException {
        List<Presc> result = new ArrayList<>();
        List<Integer> visitIds = client.listVisitIdInDateInterval(from, upto);
        for(int visitId: visitIds){
            Presc presc = probeVisit(client, visitId);
            if( presc != null ){
                result.add(presc);
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
