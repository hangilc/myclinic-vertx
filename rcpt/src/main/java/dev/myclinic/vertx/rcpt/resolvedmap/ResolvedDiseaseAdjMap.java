package dev.myclinic.vertx.rcpt.resolvedmap;

import dev.myclinic.vertx.client.Service;

import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;

public class ResolvedDiseaseAdjMap extends ResolvedMapBase {

    @MasterNameMapAnnot(candidates="の疑い")
    public int 疑い; // 8002;

    public CompletableFuture<Void> resolveAt(LocalDate at){
        return resolveAt(at, Service.api::batchResolveShuushokugoNames);
    }

    @Override
    public String toString() {
        return "ResolvedDiseaseAdjMap{" +
                "疑い=" + 疑い +
                '}';
    }
}
