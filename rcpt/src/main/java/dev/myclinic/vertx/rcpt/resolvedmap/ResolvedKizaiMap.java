package dev.myclinic.vertx.rcpt.resolvedmap;

import dev.myclinic.vertx.client.Service;

import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;

public class ResolvedKizaiMap extends ResolvedMapBase {

    public int 半切; //700010000;
    public int 大角; //700030000;
    public int 四ツ切; //700080000;

    public CompletableFuture<Void> resolveAt(LocalDate at){
        return resolveAt(at, Service.api::batchResolveKizaiNames);
    }

    @Override
    public String toString() {
        return "ResolvedKizaiMap{" +
                "半切=" + 半切 +
                ", 大角=" + 大角 +
                ", 四ツ切=" + 四ツ切 +
                '}';
    }
}
