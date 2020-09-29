package dev.myclinic.vertx.rcpt.resolvedmap;

import dev.myclinic.vertx.client.Service;

import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;

public class ResolvedDiseaseMap extends ResolvedMapBase {

    public int 急性上気道炎; // 3041;
    public int アレルギー性鼻炎; // 2517;
    public int 糖尿病; // 2500013;
    public int 低血糖発作; // 2512004;
    public int 前立腺癌; // 1859003;

    public CompletableFuture<Void> resolveAt(LocalDate at){
        return resolveAt(at, Service.api::batchResolveByoumeiNames);
    }

    @Override
    public String toString() {
        return "ResolvedDiseaseMap{" +
                "急性上気道炎=" + 急性上気道炎 +
                ", アレルギー性鼻炎=" + アレルギー性鼻炎 +
                ", 糖尿病=" + 糖尿病 +
                ", 低血糖発作=" + 低血糖発作 +
                ", 前立腺癌=" + 前立腺癌 +
                '}';
    }
}
