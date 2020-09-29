package dev.myclinic.vertx.rcpt.check;

import dev.myclinic.vertx.client.Service;
import dev.myclinic.vertx.dto.DiseaseFullDTO;
import dev.myclinic.vertx.dto.PatientDTO;
import dev.myclinic.vertx.dto.VisitFull2DTO;
import dev.myclinic.vertx.rcpt.Common;
import dev.myclinic.vertx.rcpt.resolvedmap.ResolvedMap;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.LocalDate;
import java.util.List;

// TODO: check ヘリコバクタ抗体、尿素呼気試験、除菌
public class Check {

    private Check() {
    }

    public static void run(RunEnv runEnv) {
        int year = runEnv.year;
        int month = runEnv.month;
        ResolvedMap resolvedMap = Common.getMasterMaps(LocalDate.of(year, month, 1));
        doCheck(runEnv, resolvedMap);
    }

    private static void doCheck(RunEnv runEnv, ResolvedMap resolvedMap) {
        int year = runEnv.year;
        int month = runEnv.month;
        try {
            for (int patientId : runEnv.patientIds) {
                PatientDTO patient = Service.api.getPatient(patientId).join();
                if (runEnv.verbose) {
                    System.out.printf("%04d %s%s%n", patient.patientId, patient.lastName, patient.firstName);
                }
                List<VisitFull2DTO> visits = Service.api.listVisitByPatientHavingHoken(patientId, year, month)
                        .join();
                assert visits.size() > 0;
                List<DiseaseFullDTO> diseases = Service.api.listDiseaseByPatientAtCall(patientId, year, month)
                        .execute().body();
                if (diseases == null) {
                    System.err.println("Failed to get disease list (some checks skipped). PatientID " + patientId);
                    continue;
                }
                Scope scope = new Scope(patient, visits, resolvedMap, diseases, runEnv.errorHandler, runEnv.api);
                new CheckChouzai(scope).check();
                new CheckDuplicates(scope).check();
                new CheckTokuteiShikkanKanri(scope).check();
                new CheckChoukiTouyakuKasan(scope).check();
                new CheckHandanryou(scope).check();
                new CheckShoshinSaishin(scope).check();
                new CheckRyouyouDouisho(scope).check();
                new CheckKouseishinyaku(scope).check();
                new CheckNaifuku(scope).check();
                new CheckGaiyou(scope).check();
                new CheckShohouryou(scope).check();
                new CheckShoshinByoumei(scope).check();
                new CheckSaishinByoumei(scope).check();
                new CheckByoumei(scope).check();
            }
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

}
