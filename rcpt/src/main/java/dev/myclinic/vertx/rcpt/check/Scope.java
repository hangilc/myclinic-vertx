package dev.myclinic.vertx.rcpt.check;

import dev.myclinic.vertx.consts.DiseaseEndReason;
import dev.myclinic.vertx.dto.*;
import dev.myclinic.vertx.rcpt.resolvedmap.ResolvedMap;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

class Scope {
    PatientDTO patient;
    List<VisitFull2DTO> visits;
    ResolvedMap resolvedMasterMap;
    List<DiseaseFullDTO> diseases;
    Consumer<Error> errorHandler;
    Fixer api;

    Scope() {
    }

    Scope(PatientDTO patient, List<VisitFull2DTO> visits, ResolvedMap resolvedMasterMap,
          List<DiseaseFullDTO> diseases, Consumer<Error> errorHandler, Fixer api) {
        this.patient = patient;
        this.visits = visits;
        this.resolvedMasterMap = resolvedMasterMap;
        this.diseases = diseases;
        this.errorHandler = errorHandler;
        this.api = api;
    }

    boolean hasByoumeiAt(int byoumeicode, LocalDate at) {
        for (DiseaseFullDTO disease : diseases) {
            if( disease.disease.shoubyoumeicode == byoumeicode ){
                LocalDate startDate = LocalDate.parse(disease.disease.startDate);
                if( startDate.isEqual(at) || startDate.isBefore(at) ){
                    if( disease.disease.endDate == null || disease.disease.endDate.equals("0000-00-00") ){
                        return true;
                    }
                    LocalDate endDate = LocalDate.parse(disease.disease.endDate);
                    if( endDate.isEqual(at) || endDate.isAfter(at) ){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    LocalDate getVisitDate(VisitFull2DTO visit) {
        return LocalDate.parse(visit.visit.visitedAt.substring(0, 10));
    }

    void error(String message, String fixMessage, Runnable fix) {
        Error error = new Error(patient, message, fixMessage, fix);
        errorHandler.accept(error);
    }

    DiseaseNewDTO createNewDisease(VisitFull2DTO visit, int byoumeicode, List<Integer> shuushokugocodes){
        DiseaseNewDTO dto = new DiseaseNewDTO();
        DiseaseDTO disease = new DiseaseDTO();
        disease.patientId = visit.visit.patientId;
        disease.shoubyoumeicode = byoumeicode;
        disease.startDate = visit.visit.visitedAt.substring(0, 10);
        disease.endDate = "0000-00-00";
        disease.endReason = DiseaseEndReason.NotEnded.getCode();
        dto.disease = disease;
        List<DiseaseAdjDTO> adjList = new ArrayList<>();
        if (shuushokugocodes != null) {
            shuushokugocodes.forEach(shuushokugocode -> {
                DiseaseAdjDTO adj = new DiseaseAdjDTO();
                adj.shuushokugocode = shuushokugocode;
                adjList.add(adj);
            });
        }
        dto.adjList = adjList;
        return dto;
    }

    void enterDisease(VisitFull2DTO visit, int byoumeicode) {
        enterDisease(visit, byoumeicode, Collections.emptyList());
    }

    void enterDisease(VisitFull2DTO visit, int byoumeicode, int shuushokugocode) {
        enterDisease(visit, byoumeicode, Collections.singletonList(shuushokugocode));
    }

    void enterDisease(VisitFull2DTO visit, int byoumeicode, List<Integer> shuushokugocodes) {
        DiseaseNewDTO dto = createNewDisease(visit, byoumeicode, shuushokugocodes);
        api.enterDisease(dto);
    }

    @Override
    public String toString() {
        return "Scope{" +
                "patient=" + patient +
                ", visits=" + visits +
                ", resolvedMasterMap=" + resolvedMasterMap +
                ", diseases=" + diseases +
                ", errorHandler=" + errorHandler +
                ", api=" + api +
                '}';
    }
}
